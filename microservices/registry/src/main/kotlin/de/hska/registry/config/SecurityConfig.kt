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
package de.hska.registry.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders
        .AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration
        .WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User
        .withDefaultPasswordEncoder

import java.util.Locale

/**
 * Security-Konfiguration
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Configuration
internal class SecurityConfig : WebSecurityConfigurerAdapter() {
    private val realm by lazy {
        // Name der REALM = Name des Parent-Package in Grossbuchstaben,
        // z.B. REGISTRY
        val pkgName = SecurityConfig::class.java.`package`.name
        val parentPkgName =
                pkgName.substring(0, pkgName.lastIndexOf('.'))
        parentPkgName.substring(parentPkgName.lastIndexOf('.') + 1)
                .toUpperCase(Locale.getDefault())
    }
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .anyRequest().permitAll()
            .antMatchers("/application/**").permitAll()
            .and()
            .httpBasic().realmName(realm)
            .and()
            .csrf().disable()
            .headers().frameOptions().disable()
    }

    /**
     * Einen User "admin" mit Passwort "p" bereitstellen.
     * @param auth Injiziertes Objekt der Klasse AuthenticationManagerBuilder
     */
    @Autowired
    fun configAuthentication(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
            .withUser(
                withDefaultPasswordEncoder()
                    .username("admin")
                    .password("p")
                    .roles("ACTUATOR"))
    }
}
