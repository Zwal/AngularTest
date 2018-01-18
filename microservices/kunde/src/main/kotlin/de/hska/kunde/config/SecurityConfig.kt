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

import de.hska.kunde.config.security.SimpleUser
import de.hska.kunde.config.security.SimpleUserService
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.context.ApplicationContext
import java.util.Locale
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.dropCollection
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.annotation.web.reactive
        .EnableWebFluxSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService

/**
 * Security-Konfiguration
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
// https://github.com/spring-projects/spring-security/tree/master/samples
@Configuration
@EnableWebFluxSecurity
internal class SecurityConfig {
    @Bean
    fun springWebFilterChain(http: ServerHttpSecurity) =
        http.authorizeExchange()
            .pathMatchers(POST, KUNDE_PATH).permitAll()
            .pathMatchers(GET, KUNDE_PATH, KUNDE_ID_PATH).hasRole(ADMIN)
            .pathMatchers(GET, MULTIMEDIA_ID_PATH).hasRole(KUNDE)
            .pathMatchers(PUT, KUNDE_ID_PATH, MULTIMEDIA_ID_PATH)
                    .hasRole(KUNDE)
            .pathMatchers(PATCH, KUNDE_ID_PATH).hasRole(ADMIN)
            .pathMatchers(DELETE, KUNDE_ID_PATH).hasRole(ADMIN)
                .pathMatchers(AUTH_PATH).permitAll()

            .pathMatchers(GET, ACTUATOR_PATH, "$ACTUATOR_PATH/*")
                    .hasRole(ACTUATOR)
            .pathMatchers(POST, "$ACTUATOR_PATH/*")
                    .hasRole(ACTUATOR)
                .pathMatchers(OPTIONS).permitAll()
            .and()
            .httpBasic()

            .and()
            .formLogin().disable()
            .csrf().disable()
            // FIXME Disable FrameOptions (FrameOptionsConfig): Clickjacking
            .build()

    @Bean
    fun userDetailsRepository(service: SimpleUserService,
                              ctx: ApplicationContext,
                              mongoTemplate: ReactiveMongoTemplate
    ): ReactiveUserDetailsService {
        if (ctx.environment.activeProfiles.contains(Settings.DEV)) {
            mongoTemplate.dropCollection<SimpleUser>()
                .thenMany(mongoTemplate.insertAll(SimpleUserService.USERS))
                .subscribe { LOGGER.warn("$it") }
        }

        return ReactiveUserDetailsService { service.findByUsername(it) }
    }

    private companion object {
        val ADMIN = "ADMIN"
        val KUNDE = "KUNDE"
        val ACTUATOR = "ACTUATOR"

        val KUNDE_PATH = "/"
        val KUNDE_ID_PATH = "/*"
        val MULTIMEDIA_ID_PATH = "/multimedia/*"
        val ACTUATOR_PATH = "/application"
        val AUTH_PATH ="/auth/rollen"
        val LOGGER = getLogger()

        @Suppress("unused")
        val REALM by lazy {
            // Name der REALM = Name des Parent-Package in Grossbuchstaben,
            // z.B. KUNDEN
            val pkg = SecurityConfig::class.java.`package`.name
            val parentPkg = pkg.substring(0, pkg.lastIndexOf('.'))
            parentPkg.substring(parentPkg.lastIndexOf('.') + 1)
                    .toUpperCase(Locale.getDefault())
        }
    }
}
