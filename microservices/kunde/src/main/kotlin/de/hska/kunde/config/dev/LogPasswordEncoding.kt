package de.hska.kunde.config.dev

import de.hska.kunde.config.Settings.DEV
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Description
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Einen CommandLineRunner zur Ausgabe fuer BCrypt bereitstellen.
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
internal interface LogPasswordEncoding {
    /**
     * Spring Bean, um einen CommandLineRunner fuer das Profil "dev"
     * bereitzustellen.
     * @return CommandLineRunner
     */
    @Bean
    @Description("Ausgabe fuer BCrypt")
    @Profile(DEV)
    fun logBCrypt(passwordEncoder: PasswordEncoder): CommandLineRunner {
        val log = getLogger(LogPasswordEncoding::class.java)

        return CommandLineRunner {
            val verschluesselt = passwordEncoder.encode(PASSWORD)
            log.warn("Verschluesselung von $PASSWORD:   $verschluesselt")
        }
    }

    private companion object {
        val PASSWORD = "p"
    }
}
