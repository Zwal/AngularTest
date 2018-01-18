package de.hska.kunde.service

import de.hska.kunde.db.KundeRepository
import org.springframework.stereotype.Service

/**
 * Anwendungslogik fuer Kunden.
 * @author [
 * Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Service
internal class KundeStreamService(private val repo: KundeRepository) {
    fun findAll() = repo.findAll()
}
