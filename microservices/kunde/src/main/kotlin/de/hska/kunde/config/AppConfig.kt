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
package de.hska.kunde.config

import de.hska.kunde.config.dev.DbReload
import de.hska.kunde.config.dev.LazyInit
import de.hska.kunde.config.dev.LogBasicAuth
import de.hska.kunde.config.dev.LogPasswordEncoding
import de.hska.kunde.config.dev.LogRequest
import de.hska.kunde.config.dev.MongoMappingEventsListener
import de.hska.kunde.config.security.PasswordEncoder
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker
import org.springframework.context.annotation.Configuration

// @Configuration-Klassen als Einstiegspunkt zur Konfiguration
// Mit CGLIB werden @Configuration-Klassen verarbeitet

@Configuration
@EnableCaching
@EnableCircuitBreaker
internal class AppConfig :
    DbReload,
    KafkaConfig,
    LazyInit,
    LogBasicAuth,
    LogPasswordEncoding,
    LogRequest,
    MailConfig,
    MongoMappingEventsListener,
    PasswordEncoder,
    WebServerReactiveConfig,
    CorsConfig
