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
import de.hska.kunde.rest.util.contentType
import de.hska.kunde.service.KundeMultimediaService
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType.parseMediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyExtractors.toDataBuffers
import org.springframework.web.reactive.function.BodyExtractors.toMultipartData
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse
        .badRequest
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

@Component
internal class KundeMultimediaHandler(
        private val service: KundeMultimediaService) {
    fun download(request: ServerRequest): Mono<ServerResponse> {
        val id = request.pathVariable(ID_PATH_VAR)
        return service.findMedia(id)
            .flatMap {
                val length = it!!.contentLength()
                LOGGER.trace("length={}", length)
                val mediaType = parseMediaType(it.contentType)
                LOGGER.trace("mediaType={}", mediaType)
                ok().contentLength(length)
                    .contentType(mediaType)
                    .body(it.toMono())
            }
            .switchIfEmpty(notFound().build())
    }

    fun upload(request: ServerRequest): Mono<ServerResponse> {
        val contentType = request.contentType() ?: return badRequest().build()
        val id = request.pathVariable(ID_PATH_VAR)

        return if (contentType.startsWith("multipart/form-data"))
            uploadMultipart(request, id)
        else
            uploadBinary(request, id, contentType)
    }

    private fun uploadMultipart(request: ServerRequest, id: String) =
        // https://github.com/sdeleuze/webflux-multipart/blob/master/src/...
        //       ...main/java/com/example/MultipartRoute.java
        request.body(toMultipartData())
            .flatMap {
                val part = it.toSingleValueMap()["file"]
                val contentType = part?.contentType()
                val content = part?.content() ?: Flux.empty()
                save(content, id, contentType)
            }
            .switchIfEmpty(badRequest().build())

    private fun save(data: Flux<DataBuffer>,
                     id: String,
                     contentType: String?): Mono<ServerResponse> {
        if (contentType == null) {
            return badRequest().build()
        }

        // Flux<DataBuffer> als Mono<List<DataBuffer>>
        return data.collectList()
                .map { it[0] }
                .flatMap {
                    val inputStream = it!!.asInputStream()
                    service.save(inputStream, id, contentType)
                }
                .flatMap { noContent().build() }
    }

    private fun uploadBinary(request: ServerRequest,
                             id: String,
                             contentType: String): Mono<ServerResponse> {
        val data = request.body(toDataBuffers())
        return save(data, id, contentType)
    }

    private companion object {
        val LOGGER = getLogger()
    }
}
