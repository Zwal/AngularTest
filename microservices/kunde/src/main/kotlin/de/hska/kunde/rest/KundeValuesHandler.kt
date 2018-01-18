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

import de.hska.kunde.Application.Companion.ID_PATH_VAR
import de.hska.kunde.Application.Companion.PREFIX_PATH_VAR
import de.hska.kunde.service.KundeValuesService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Component
internal class KundeValuesHandler(private val service: KundeValuesService) {
    fun findNachnamenByPrefix(request: ServerRequest): Mono<ServerResponse> {
        val prefix = request.pathVariable(PREFIX_PATH_VAR)
        return service.findNachnamenByPrefix(prefix)
            .collectList()
            .flatMap {
                if (it.isEmpty()) notFound().build() else ok().body(it.toMono())
            }
    }

    fun findEmailsByPrefix(request: ServerRequest): Mono<ServerResponse> {
        val prefix = request.pathVariable(PREFIX_PATH_VAR)
        return service.findEmailsByPrefix(prefix)
            .collectList()
            .flatMap {
                if (it.isEmpty()) notFound().build() else ok().body(it.toMono())
            }
    }

    fun findVersionById(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable(ID_PATH_VAR)
        return service.findVersionById(id)
            .map { it.toString() }
            // version als String: kein Deserialisieren wie bei Entity-Klassen
            .flatMap { ok().body(it.toMono()) }
    }
}
