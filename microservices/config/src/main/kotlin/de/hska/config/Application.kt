/* Copyright (C) 2016 - 2017 Juergen Zimmermann, Hochschule Karlsruhe
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
package de.hska.config

import de.hska.config.config.Settings.BANNER
import de.hska.config.config.Settings.DEV_PROFILE
import de.hska.config.config.Settings.PROPS
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

/**
 * Start des Config-Servers
 */
@SpringBootApplication
@EnableConfigServer
internal class Application

/**
 * Start des Config-Servers
 * @param args Zusaetzliche Argumente fuer den Config-Server
 */

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<Application>(*args) {
        setBanner(BANNER)
        setDefaultProperties(PROPS)
        setAdditionalProfiles(DEV_PROFILE)
    }
}
