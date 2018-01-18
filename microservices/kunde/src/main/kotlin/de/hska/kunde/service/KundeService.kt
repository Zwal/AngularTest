/*
 * Copyright (C) 2016 - 2017 Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.hska.kunde.service

import de.hska.kunde.config.security.SimpleUser
import de.hska.kunde.config.security.SimpleUserService
import de.hska.kunde.db.KundeRepository
import de.hska.kunde.db.CriteriaUtil.getCriteria
import de.hska.kunde.db.update
import de.hska.kunde.entity.Kunde
import de.hska.kunde.mail.MailSender
import java.time.Duration.ofMillis
import java.util.Locale
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Lazy
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.util.MultiValueMap
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.util.UUID.randomUUID

/**
 * Anwendungslogik fuer Kunden.
 * <img src="../../../../../images/KundeService.png" alt="Klassendiagramm">
 * </img>
 * @author [
 * Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Service
// FIXME https://youtrack.jetbrains.com/issue/KT-11235
@CacheConfig(cacheNames = ["kunde_id"])
internal class KundeService (
        private val repo: KundeRepository,
        // Annotation im zugehoerigen Parameter des Java-Konstruktors
        @param:Lazy private val mongoTemplate: ReactiveMongoTemplate,
        @param:Lazy private val userService: SimpleUserService,
        @param:Lazy private val mailSender: MailSender) {

    /**
     * Einen Kunden anhand seiner ID suchen
     * @param id Die Id des gesuchten Kunden
     * @return Der gefundene Kunde oder ein leeres Mono-Objekt
     */
    @Cacheable(key = "#id")
    fun findById(id: String) = repo.findById(id).timeout(TIMEOUT_SHORT)

    /**
     * Kunden anhand von Suchkriterien ermitteln
     * @param queryParams Suchkriterien
     * @return Gefundene Kunden
     */
    fun find(queryParams: MultiValueMap<String, String>): Flux<Kunde> {
        if (queryParams.isEmpty()) {
            return repo.findAll()
        }

        if (queryParams.size == 1) {
            val emailList = queryParams["email"]
            if (emailList != null) {
                return if (emailList.size == 1)
                    repo.findByEmail(emailList[0]).flux()
                else
                    Flux.empty()
            }
        }

        val criteria = getCriteria(queryParams)
        if (criteria.contains(null)) {
            return Flux.empty()
        }

        val query = Query()
        criteria.filter { it != null }
                .forEach { query.addCriteria(it!!) }
        LOGGER.debug("{}", query)
        // http://www.baeldung.com/spring-data-mongodb-tutorial
        return mongoTemplate.find<Kunde>(query).timeout(TIMEOUT_LONG)
    }

    /**
     * Einen neuen Kunden anlegen
     * @param kunde Das Objekt des neu anzulegenden Kunden
     * @return Der neu angelegte Kunde mit generierter ID
     */
    fun create(kunde: Kunde): Mono<Kunde> {
        // Account nicht @NotNull: nicht in der Mongo-Collection gespeichert
        kunde.user ?: throw InvalidAccountException()

        val email = kunde.email
        return repo.findByEmail(email)
            .timeout(TIMEOUT_SHORT)
            .map<Kunde> { throw EmailExistsException(email) }
            .switchIfEmpty(kunde.toMono())
            .flatMap { createUser(kunde) }
            .flatMap { create(kunde, it) }
            .doOnSuccess { mailSender.send(it) }
    }

    private fun createUser(kunde: Kunde): Mono<SimpleUser>? {
        val user = SimpleUser(
                id = null,
                username = kunde.user!!.username,
                password = kunde.user!!.password,
                authorities = listOf(SimpleGrantedAuthority("ROLE_KUNDE")))
        LOGGER.trace("User wird angelegt: {}", user)
        return userService.create(user)
                .timeout(TIMEOUT_SHORT)
    }

    private fun create(kunde: Kunde, user: SimpleUser): Mono<Kunde>? {
        val neuerKunde = kunde.copy(
                email = kunde.email.toLowerCase(Locale.getDefault()),
                username = user.username,
                id = randomUUID().toString())
        neuerKunde.user = user
        LOGGER.trace("Kunde mit username: {}", kunde)
        return repo.save(neuerKunde).timeout(TIMEOUT_SHORT)
    }

    /**
     * Einen vorhandenen Kunden aktualisieren
     * @param kunde Das Objekt mit den neuen Daten
     * @param version Versionsnummer
     * @return Der aktualisierte Kunde oder ein leeres Mono-Objekt, falls
     * es keinen Kunden mit der angegebenen ID gibt
     */
    @CacheEvict(key = "#kunde.id")
    fun update(kunde: Kunde, version: String): Mono<Kunde> {
        val id = kunde.id ?: return Mono.empty()

        return repo.findById(id)
            .timeout(TIMEOUT_SHORT)
            .flatMap { kundeDb ->
                LOGGER.trace("update: kundeDb={}", kundeDb)
                checkVersion(kundeDb, version)
                checkEmail(kundeDb, kunde.email)
                    .switchIfEmpty(kundeDb.toMono())
                    .flatMap { update(kundeDb, kunde) }
            }
    }

    private fun checkVersion(kundeDb: Kunde, versionStr: String) {
        // Gibt es eine neuere Version in der DB?
        val version = try {
            versionStr.toInt()
        } catch (e: NumberFormatException) {
            LOGGER.debug("Ungueltige Version: {}", versionStr)
            throw InvalidVersionException(versionStr, e)
        }

        val versionDb = kundeDb.getVersion()!!
        if (version < versionDb) {
            throw InvalidVersionException(versionStr)
        }
    }

    private fun checkEmail(kundeDb: Kunde, neueEmail: String): Mono<Kunde> {
        // Hat sich die Emailadresse ueberhaupt geaendert?
        if (kundeDb.email == neueEmail) {
            return Mono.empty()
        }

        // Gibt es die neue Emailadresse bei einem existierenden Kunden?
        return repo.findByEmail(neueEmail)
                .timeout(TIMEOUT_SHORT)
                .map<Kunde> { throw EmailExistsException(neueEmail) }
    }

    private fun update(kundeDb: Kunde, kunde: Kunde): Mono<Kunde>? {
        kundeDb.update(kunde)
        LOGGER.trace("Abspeichern des geaenderten Kunden: {}",
                kundeDb)
        return repo.save(kundeDb).timeout(TIMEOUT_SHORT)
    }

    /**
     * Einen vorhandenen Kunden lschen
     * @param id Die ID des zu lschenden Kunden
     * @return true falls es zur ID ein Kundenobjekt gab, das gelscht
     * wurde; false sonst
     */
    // erfordert zusaetzliche Konfiguration in SecurityConfig
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @CacheEvict(key = "#id")
    fun deleteById(id: String) =
        // EmptyResultDataAccessException bei delete(), falls es zur gegebenen
        // ID kein Objekt gibt
        // http://docs.spring.io/spring/docs/current/javadoc-api/org/...
        // ...springframework/dao/EmptyResultDataAccessException.html
        repo.findById(id)
            .timeout(TIMEOUT_SHORT)
            .delayUntil { repo.deleteById(id).timeout(TIMEOUT_SHORT) }

    /**
     * Einen vorhandenen Kunden lschen
     * @param email Die Email des zu loeschenden Kunden
     * @return true falls es zur Email ein Kundenobjekt gab, das geloescht
     * wurde; false sonst
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun deleteByEmail(email: String) = repo.deleteByEmail(email)

    companion object {
        private val LOGGER = getLogger()
        private val TIMEOUT_SHORT = ofMillis(500)
        private val TIMEOUT_LONG = ofMillis(2000)
    }
}
