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

import org.springframework.boot.context.properties.ConfigurationProperties
import java.lang.Boolean.FALSE
import java.util.Locale
import java.util.Properties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Description
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

/**
 * Spring-Konfiguration fr den SMTP-Zugriff auf einen Mailserver
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
internal interface MailConfig {
    /**
     * Spring-Bean fuer das Verschicken von Emails.
     * @param config Konfigurations-Properties fuer Hostname und
     *               Port des Mailservers
     * @return Das konfigurierte Objekt, um Emails zu verschicken.
     */
    @Bean
    @Description("JavaMailSender")
    fun javaMailSender(config: MailProps): JavaMailSender {
        // set-Methoden sind im Interface JavaMailSender nicht deklariert
        return JavaMailSenderImpl().apply {
            host = config.host
            port = config.port

            // https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/...
            // ...package-summary.html
            val falseStr = FALSE.toString().toLowerCase(Locale.getDefault())
            val properties = Properties().apply {
                setProperty("mail.transport.protocol", "smtp")
                setProperty("mail.smtp.auth", falseStr)
                setProperty("mail.smtp.starttls.enable", falseStr)
                setProperty("mail.debug", falseStr)
            }
            javaMailProperties = properties
        }
    }
}

@Component
// FIXME Spring Boot 2.1
// https://github.com/spring-projects/spring-boot/issues/8762
// https://github.com/spring-projects/spring-boot/issues/1254
@ConfigurationProperties(prefix = "mail")
internal data class MailProps (
    var host: String = "localhost",
    var port: Int = 25000,
    var from: String = "",
    var sales: String = "",
    var topic: String = "")
