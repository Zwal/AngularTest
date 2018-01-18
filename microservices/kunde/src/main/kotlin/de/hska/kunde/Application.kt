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

package de.hska.kunde

import de.hska.kunde.config.Settings.BANNER
import de.hska.kunde.config.Settings.PROPS
import de.hska.kunde.config.security.AuthHandler
import de.hska.kunde.entity.Kunde
import de.hska.kunde.rest.KundeHandler
import de.hska.kunde.rest.KundeMultimediaHandler
import de.hska.kunde.rest.KundeStreamHandler
import de.hska.kunde.rest.KundeValuesHandler
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.WebApplicationType.REACTIVE
import org.springframework.boot.system.ApplicationPidFileWriter
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType.TEXT_EVENT_STREAM
import org.springframework.web.reactive.function.server.router

// @Configuration @EnableAutoConfiguration @ComponentScan
@SpringBootApplication
internal class Application {
    @Bean
    internal fun router(handler: KundeHandler,
               streamHandler: KundeStreamHandler,
               multimediaHandler: KundeMultimediaHandler,
               valuesHandler: KundeValuesHandler,
               authHandler: AuthHandler) = router {
        // https://github.com/spring-projects/spring-framework/blob/master/...
        //       ..spring-webflux/src/main/kotlin/org/springframework/web/...
        //       ...reactive/function/server/RouterFunctionDsl.kt
        "/".nest {
            GET("/", handler::find)
            GET("/$ID_PATH_PATTERN", handler::findById)

            POST("/", handler::create)
            PUT("/$ID_PATH_PATTERN", handler::update)
            PATCH("/$ID_PATH_PATTERN", handler::patch)

            DELETE("/$ID_PATH_PATTERN", handler::deleteById)
            DELETE("/", handler::deleteByEmail)

            // Fuer "Software Engineering" und Android
            GET("/nachname/{$PREFIX_PATH_VAR}",
                    valuesHandler::findNachnamenByPrefix)
            GET("/email/{$PREFIX_PATH_VAR}",
                    valuesHandler::findEmailsByPrefix)
            GET("/version/$ID_PATH_PATTERN",
                    valuesHandler::findVersionById)
        }

        (accept(TEXT_EVENT_STREAM) and "/stream").nest {
            GET("/", streamHandler::findAll)
        }

        "/multimedia".nest {
            GET("/$ID_PATH_PATTERN", multimediaHandler::download)
            PUT("/$ID_PATH_PATTERN", multimediaHandler::upload)
        }

        "/auth".nest {
            GET("/rollen", authHandler::findEigeneRollen)
        }

        // ggf. weitere Routen: z.B. HTML mit ThymeLeaf, Mustache, FreeMarker
    }
    // ggf. noch Filterung der Requests
    .filter { request, next ->
        LOGGER.trace("Filter vor dem Aufruf eines Handlers: {}",
                request.uri())
        next.handle(request)
    }

    companion object {
        val ID_PATH_VAR = "id"
        private val ID_PATH_PATTERN = "{$ID_PATH_VAR:${Kunde.ID_PATTERN}}"

        val PREFIX_PATH_VAR = "nachname"
        private val LOGGER = getLogger()
    }
}

/**
 * Start des Microservice
 * @param args Zusaetzliche Argumente fuer den Microservice
 */
fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<Application>(*args) {
        webApplicationType = REACTIVE
        setBanner(BANNER)
        setDefaultProperties(PROPS)
        addListeners(ApplicationPidFileWriter())
    }
}
