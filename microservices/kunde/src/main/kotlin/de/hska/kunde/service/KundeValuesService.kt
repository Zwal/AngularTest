package de.hska.kunde.service

import de.hska.kunde.db.KundeRepository
import org.springframework.cache.annotation.CacheConfig
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * Anwendungslogik fuer Kunden.
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Service
// FIXME https://youtrack.jetbrains.com/issue/KT-11235
@CacheConfig(cacheNames = ["kunde_id"])
internal class KundeValuesService(private val repo: KundeRepository) {
    /**
     * Nachnamen anhand eines Praefix ermitteln. Projektionen in Spring Data
     * wuerden "nullable" Properties in den Data-Klassen erfordern.
     * @param prefix Praefix fuer Nachnamen
     * @return Gefundene Nachnamen
     */
    fun findNachnamenByPrefix(prefix: String) =
        repo.findByNachnameStartingWithIgnoreCase(prefix)
            .timeout(TIMEOUT_SHORT)
            .map { it.nachname }
            .distinct()

    /**
     * Emailadressen anhand eines Praefix ermitteln
     * @param prefix Praefix fuer Email
     * @return Gefundene Emailadressen
     */
    fun findEmailsByPrefix(prefix: String) =
        repo.findByEmailStartingWithIgnoreCase(prefix)
            .timeout(TIMEOUT_SHORT)
            .map { it.email }

    /**
     * Version zur Kunde-ID ermitteln
     * @param id Kunde-ID
     * @return Versionsnummer
     */
    fun findVersionById(id: String) =
        repo.findById(id)
            .timeout(TIMEOUT_SHORT)
            .map { it.getVersion()!! }

    private companion object {
        val TIMEOUT_SHORT = Duration.ofMillis(500)
    }
}
