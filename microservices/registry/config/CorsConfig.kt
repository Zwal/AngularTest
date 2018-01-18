/*
 * Copyright (C) 2018 Juergen Zimmermann, Hochschule Karlsruhe
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
package de.hska.kunde.config

import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.util.pattern.PathPatternParser
import javax.ws.rs.HttpMethod.DELETE
import javax.ws.rs.HttpMethod.GET
import javax.ws.rs.HttpMethod.OPTIONS
import javax.ws.rs.HttpMethod.POST
import javax.ws.rs.HttpMethod.PUT

// https://docs.spring.io/spring/docs/current/spring-framework-reference/...
//         ...web-reactive.html#webflux-cors-webfilter
interface CorsConfig {
    @Bean
    fun corsFilter(): CorsWebFilter {
        val source = UrlBasedCorsConfigurationSource(PathPatternParser()).apply {
            val config = CorsConfiguration().apply {
                allowCredentials = true
                allowedOrigins = listOf("https://localhost")
                allowedMethods = listOf(GET, POST, PUT, DELETE, OPTIONS)
                addAllowedMethod("PATCH")
                allowedHeaders = listOf(
                        "origin",
                        "content-type",
                        "accept",
                        "authorization",
                        "access-control-allow-origin",
                        "access-control-allow-methods",
                        "access-control-allow-headers",
                        "allow",
                        "content-length",
                        "date",
                        "last-modified",
                        "if-modified-since")
            }
            registerCorsConfiguration("/**", config)
        }

        return CorsWebFilter(source)
    }
}
