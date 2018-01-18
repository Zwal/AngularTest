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

import de.hska.kunde.rest.util.authorization
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import reactor.core.publisher.Mono
import java.util.Base64

@Component
internal class AuthHandler (private val repo: SimpleUserRepository) {
    fun findEigeneRollen(request: ServerRequest): Mono<ServerResponse> {
        val authorization = request.authorization()
        return if (authorization == null) {
            status(UNAUTHORIZED).build()
        } else {
            val username = getUsername(authorization)
            repo.findByUsername(username)
                .flatMap {
                    val rollen = it.authorities.map { it.authority }
                    ok().syncBody(rollen)
                }
                .switchIfEmpty(status(UNAUTHORIZED).build())
        }
    }

    // Aus dem Header fuer BASIC-Authentifizierung den Username extrahieren
    private fun getUsername(authValue: String?): String? {
        if (authValue == null ||
                !authValue.toLowerCase().startsWith("basic ")) {
            return null
        }
        val base64Encoded = authValue.substringAfter(' ')
        val decoded = Base64.getDecoder().decode(base64Encoded)
        val decodedStr = String(decoded)
        if (!decodedStr.contains(':')) {
            return null
        }
        return decodedStr.split(':')[0]
    }
}
