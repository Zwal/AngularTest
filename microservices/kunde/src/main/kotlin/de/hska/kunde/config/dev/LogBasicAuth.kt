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
package de.hska.kunde.config.dev

import de.hska.kunde.config.Settings.DEV
import java.util.Base64.getEncoder
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Description
import org.springframework.context.annotation.Profile

/**
 * Einen CommandLineRunner zur Ausgabe fuer BASIC-Authentifizierung
 * bereitstellen.
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
internal interface LogBasicAuth {
    /**
     * Spring Bean, um einen CommandLineRunner fuer das Profil "dev"
     * bereitzustellen.
     * @return CommandLineRunner
     */
    @Bean
    @Qualifier("LogBasicAuthRunner")
    @Description("Ausgabe fuer BASIC-Authentifizierung")
    @Profile(DEV)
    fun logBasicAuth(): CommandLineRunner {
        val logger = getLogger(LogBasicAuth::class.java)

        return CommandLineRunner {
            val input =
                "$USERNAME:$PASSWORD".toByteArray(charset("ISO-8859-1"))
            val encoded = "Basic ${getEncoder().encodeToString(input)}"
            logger.warn("BASIC Authentication   $USERNAME:$PASSWORD   $encoded")
        }
    }

    private companion object {
        val USERNAME = "admin"
        val PASSWORD = "p"
    }
}
