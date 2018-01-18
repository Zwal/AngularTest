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
package de.hska.config.config

import org.springframework.boot.Banner
import org.springframework.boot.SpringBootVersion
import org.springframework.core.SpringVersion
import org.springframework.security.core.SpringSecurityCoreVersion

@Suppress("MagicNumber")
internal object Settings {
    private val VERSION = "1.0"
    val DEV_PROFILE = "dev"

    val BANNER = Banner { _, _, out ->
        out.println("""
            |   ______            _____
            |  / ____/___  ____  / __(_)___ _
            | / /   / __ \/ __ \/ /_/ / __ `/
            | \____/\____/_/ /_/_/ /_/\__, /
            |                        /____/
            |
            |Version           $VERSION
            |Spring Boot       ${SpringBootVersion.getVersion()}
            |Spring Security   ${SpringSecurityCoreVersion.getVersion()}
            |Spring Framework  ${SpringVersion.getVersion()}
            |JDK               ${System.getProperty("java.version")}
            |""".trimMargin("|"))
    }

    private val PORT = 8888
    private val appName by lazy {
        val pkgName = Settings::class.java.`package`.name
        val parentPkgName =
                pkgName.substring(0, pkgName.lastIndexOf('.'))
        parentPkgName.substring(parentPkgName.lastIndexOf('.') + 1)
    }

    val PROPS = hashMapOf(
        "server.port" to PORT,
        "error.whitelabel.enabled" to false,
        "spring.application.name" to appName,
        "logging.path" to "build",
        "endpoints.shutdown.web.enabled" to true,
        "management.security.enabled" to false)
}
