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
package de.hska.kunde.config.security

import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.toMono
import java.util.UUID.randomUUID

/**
 * Service-Klasse, um Accounts zu suchen.
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Service
internal class SimpleUserService (
        private val repo: SimpleUserRepository,
        private val passwordEncoder: PasswordEncoder) {

    /**
     * Zu einem gegebenen Username wird der zugehoerige User gesucht.
     * @param username Username des gesuchten Users
     * @return Der gesuchte User
     */
    fun findByUsername(username: String?) =
        repo.findByUsername(username)
                .cast(UserDetails::class.java)
                .switchIfEmpty(NotAuthenticatedException().toMono())

    /**
     * Einen neuen User anlegen
     * @param user Der neue User
     */
    fun create(user: SimpleUser) =
        repo.findByUsername(user.username)
            .hasElement()
            .flatMap {
                if (it) {
                    throw UsernameExistsException(user.username)
                }

                // Die Account-Informationen des Kunden in Account-Informationen
                // fuer die Security-Komponente transformieren
                val password = passwordEncoder.encode(user.password)
                val authorities = user.authorities!!
                        .map { SimpleGrantedAuthority(it.authority) }
                val neuerUser = SimpleUser(
                        id = randomUUID().toString(),
                        username = user.username,
                        password = password,
                        authorities = authorities)
                LOGGER.trace("neuerUser = {}", neuerUser)
                repo.save(neuerUser)
            }

    companion object {
        private val KUNDE = "ROLE_KUNDE"
        private val ADMIN = "ROLE_ADMIN"
        private val ACTUATOR = "ROLE_ACTUATOR"

        private val PASSWORD =
            "{bcrypt}" +
            "\$2a\$10\$csH5eXtni40aoCepKV.JreDTxwJi0xYzH6rPZ8bdtyTeXLbQ8y2vq"
        val USERS = listOf(
                SimpleUser(
                    id = "10000000-0000-0000-0000-000000000000",
                    username = "admin",
                    password = PASSWORD,
                    authorities = listOf(
                        SimpleGrantedAuthority(ADMIN),
                        SimpleGrantedAuthority(KUNDE),
                        SimpleGrantedAuthority(ACTUATOR))),
                SimpleUser(
                    id = "10000000-0000-0000-0000-000000000001",
                    username = "alpha1",
                    password = PASSWORD,
                    authorities = listOf(SimpleGrantedAuthority(KUNDE))),
                SimpleUser(
                    id = "10000000-0000-0000-0000-000000000002",
                    username = "alpha2",
                    password = PASSWORD,
                    authorities = listOf(SimpleGrantedAuthority(KUNDE))),
                SimpleUser(
                    id = "10000000-0000-0000-0000-000000000003",
                    username = "alpha3",
                    password = PASSWORD,
                    authorities = listOf(SimpleGrantedAuthority(KUNDE))),
                SimpleUser(
                    id = "10000000-0000-0000-0000-000000000004",
                    username = "delta",
                    password = PASSWORD,
                    authorities = listOf(SimpleGrantedAuthority(KUNDE))),
                SimpleUser(
                    id = "10000000-0000-0000-0000-000000000005",
                    username = "epsilon",
                    password = PASSWORD,
                    authorities = listOf(SimpleGrantedAuthority(KUNDE))),
                SimpleUser(
                    id = "10000000-0000-0000-0000-000000000006",
                    username = "phi",
                    password = PASSWORD,
                    authorities = listOf(SimpleGrantedAuthority(KUNDE)))
        )

        private val LOGGER = getLogger()
    }
}
