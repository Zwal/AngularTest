/*
 * Copyright (C) 2017 Juergen Zimmermann, Hochschule Karlsruhe
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
package de.hska.kunde.rest

import com.fasterxml.jackson.core.JsonParseException
import de.hska.kunde.Application.Companion.ID_PATH_VAR
import de.hska.kunde.config.security.UsernameExistsException
import de.hska.kunde.entity.Kunde
import de.hska.kunde.entity.setId
import de.hska.kunde.rest.util.PatchOperation
import de.hska.kunde.rest.util.PatchValidator.validate
import de.hska.kunde.rest.util.ifMatch
import de.hska.kunde.rest.util.ifNoneMatch
import de.hska.kunde.rest.util.itemLinks
import de.hska.kunde.rest.util.singleLinks
import de.hska.kunde.service.EmailExistsException
import de.hska.kunde.service.InvalidAccountException
import de.hska.kunde.service.InvalidVersionException
import de.hska.kunde.service.KundeService
import org.apache.logging.log4j.LogManager.getLogger
import java.net.URI
import javax.validation.Validator
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_MODIFIED
import org.springframework.http.HttpStatus.PRECONDITION_FAILED
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToFlux
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse
        .badRequest
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import reactor.core.publisher.Mono
import reactor.core.publisher.onErrorResume
import reactor.core.publisher.toMono

// Eine Handler-Function nimmt einen Request entgegen und erstellt den Response
@Component
@Suppress("TooManyFunctions")
internal class KundeHandler(private val service: KundeService,
                            private val validator: Validator) {
    fun findById(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable(ID_PATH_VAR)

        return service.findById(id)
            .flatMap { kundeToOK(it, request) }
            .switchIfEmpty(notFound().build())
    }

    private fun kundeToOK(kunde: Kunde,
                          request: ServerRequest): Mono<ServerResponse> {
        val version = kunde.getVersion()
        val versionHeader = request.ifNoneMatch()
        return if (versionHeader != null &&
                compareVersion(version, versionHeader)) {
            status(NOT_MODIFIED).build()
        } else {
            kunde._links = request.uri().singleLinks()

            // Entity Tag, um Aenderungen an der angeforderten
            // Ressource erkennen zu koennen.
            // Client: GET-Requests mit Header "If-None-Match"
            //         ggf. Response mit Statuscode NOT MODIFIED (s.o.)
            ok().eTag("\"$version\"").body(kunde.toMono())
        }
    }

    private fun compareVersion(version: Int?, versionHeader: String): Boolean {
        if (version == null) {
            return false
        }
        val versionHeaderInt = try {
            versionHeader.toInt()
        } catch (e: NumberFormatException) {
            return false
        }
        return versionHeaderInt == version
    }

    // Idee: Mono<List<Kunde>> statt Flux<Kunde>
    fun find(request: ServerRequest): Mono<ServerResponse> {
        val queryParams = request.queryParams()

        // https://stackoverflow.com/questions/45903813/...
        //     ...webflux-functional-how-to-detect-an-empty-flux-and-return-404
        val kunden = service.find(queryParams)
            .map {
                it.links = request.uri().itemLinks(it.id!!)
                it
            }
            .collectList()

        return kunden.flatMap {
                    if (it.isEmpty())
                        notFound().build()
                    else
                        ok().body(it.toMono())
                }
    }

    fun create(request: ServerRequest) =
        request.bodyToMono<Kunde>()
            .flatMap { create(it, request.uri()) }
            .onErrorResume(InvalidAccountException::class) {
                // FIXME Warum geht nicht body() mit toMono()?
                badRequest().syncBody(it.message ?: "")
            }
            .onErrorResume(EmailExistsException::class) {
                badRequest().syncBody(it.message ?: "")
            }
            .onErrorResume(UsernameExistsException::class) {
                badRequest().syncBody(it.message ?: "")
            }
            .onErrorResume(DecodingException::class) {
                handleDecodingException(it)
            }

    private fun create(kunde: Kunde, uri: URI): Mono<ServerResponse> {
        val violations = validator.validate(kunde).map { it.message }
        return if (violations.isEmpty()) {
            LOGGER.trace("Kunde wird abgespeichert: {}", kunde)
            service.create(kunde).flatMap {
                LOGGER.trace("Kunde ist abgespeichert: {}", it)
                val location = URI("$uri/${it.id}")
                LOGGER.trace("Location URI: {}", location)
                created(location).build()
            }
        } else {
            LOGGER.trace(VIOLATIONS, violations)
            badRequest().body(violations.toMono())
        }
    }

    private fun handleDecodingException(e: DecodingException
    ): Mono<ServerResponse> {
        val exception = e.cause
        return if (exception is JsonParseException) {
            LOGGER.debug(exception.message)
            badRequest().syncBody(exception.message ?: "")
        } else {
            status(INTERNAL_SERVER_ERROR).build()
        }
    }

    fun update(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable(ID_PATH_VAR)
        val version = request.ifMatch() ?:
                return status(PRECONDITION_FAILED)
                        .body("Versionsnummer fehlt".toMono())
        return request.bodyToMono<Kunde>()
            .flatMap { update(it, id, version) }
            .switchIfEmpty(notFound().build())
            .onErrorResume(EmailExistsException::class) {
                badRequest().syncBody(it.message ?: "")
            }
            .onErrorResume(InvalidVersionException::class) {
                status(PRECONDITION_FAILED).syncBody(it.message ?: "")
            }
            .onErrorResume(DecodingException::class) {
                handleDecodingException(it)
            }
    }

    private fun update(kunde: Kunde,
                       id: String,
                       version: String): Mono<ServerResponse> {
        val violations = validator.validate(kunde).map { it.message }
        return if (violations.isEmpty()) {
            // @JsonIgnore bei Kunde.id
            kunde.setId(id)
            service.update(kunde, version)
                .flatMap {
                    LOGGER.trace("Kunde aktualisiert: {}", it)
                    noContent().eTag("\"${it.getVersion()}\"").build()
                }
        } else {
            LOGGER.trace(VIOLATIONS, violations)
            badRequest().body(violations.toMono())
        }
    }

    fun patch(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable(ID_PATH_VAR)
        val version = request.ifMatch() ?:
                return status(PRECONDITION_FAILED)
                        .body("Versionsnummer fehlt".toMono())
        LOGGER.trace("Versionsnummer $version")

        return request.bodyToFlux<PatchOperation>()
            // Die einzelnen Patch-Operationen als Liste in einem Mono
            .collectList()
            .flatMap { patchOps ->
                service.findById(id)
                    .flatMap { patch(it, patchOps, version) }
                    .switchIfEmpty(notFound().build())
                    .onErrorResume(EmailExistsException::class) {
                        badRequest().syncBody(it.message ?: "")
                    }
                    .onErrorResume(InvalidVersionException::class) {
                        status(PRECONDITION_FAILED).syncBody(it.message ?: "")
                    }
                    .onErrorResume(DecodingException::class) {
                        handleDecodingException(it)
                    }
            }
    }

    private fun patch(kundeDb: Kunde,
                      patchOps: List<PatchOperation>,
                      version: String): Mono<ServerResponse> {
        LOGGER.trace("Kunde aus DB: {}", kundeDb)
        val validateResult = validate(kundeDb, patchOps, validator)
        val kundeUpdated = validateResult.first
        val violations = validateResult.second

        return if (violations.isEmpty()) {
            LOGGER.trace("Kunde mit Patch-Ops: {}", kundeUpdated)
            service.update(kundeUpdated, version)
                .flatMap {
                    noContent().eTag("\"${it.getVersion()}\"").build()
                }
        } else {
            LOGGER.trace(VIOLATIONS, violations)
            badRequest().body(violations.toMono())
        }
    }

    fun deleteById(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable(ID_PATH_VAR)
        return service.deleteById(id)
            .flatMap { noContent().build() }
            .switchIfEmpty(notFound().build())
    }

    fun deleteByEmail(request: ServerRequest): Mono<ServerResponse> {
        val email = request.queryParam("email")
        return if (email.isPresent) {
            service.deleteByEmail(email.get())
                .flatMap { noContent().build() }
                .switchIfEmpty(notFound().build())
        } else {
            notFound().build()
        }
    }

    private companion object {
        val LOGGER = getLogger()
        val VIOLATIONS = "Violations: {}"
    }
}
