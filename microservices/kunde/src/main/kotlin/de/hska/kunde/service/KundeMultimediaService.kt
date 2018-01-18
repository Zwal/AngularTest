package de.hska.kunde.service

import de.hska.kunde.db.KundeRepository
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.io.InputStream
import java.time.Duration

/**
 * Anwendungslogik fuer Kunden.
 * @author [
 * Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Service
internal class KundeMultimediaService(private val repo: KundeRepository,
                                private val gridFsTemplate: GridFsTemplate) {
    /**
     * Multimediale Datei (Bild oder Video) zu einem Kunden mit gegebener ID
     * ermitteln
     * @param kundeId Kunde-ID
     * @return Multimediale Datei, falls sie existiert. Sonst empty().
     */
    fun findMedia(kundeId: String) =
        repo.existsById(kundeId)
            .timeout(TIMEOUT_SHORT)
            .flatMap {
                val file = if (it) gridFsTemplate.getResource(kundeId) else null
                Mono.justOrEmpty(file)
            }

    /**
     * Multimediale Daten aus einem Inputstream werden persistent zur gegebenen
     * Kunden-ID abgespeichert. Der Inputstream wird am Ende geschlossen.
     * @param kundeId Kunde-ID
     * @param inputStream Inputstream mit multimedialen Daten.
     * @param contentType MIME-Type, z.B. image/png
     * @return ID der neuangelegten multimedialen Datei
     */
    fun save(inputStream: InputStream, kundeId: String, contentType: String) =
        // Nur zu einem existierenden Kunden werden multimediale Daten abgelegt
        repo.existsById(kundeId)
            .timeout(TIMEOUT_SHORT)
            .flatMap {
                // TODO Pruefen, ob der MIME-Type auch stimmt
                val idGridFS = if (it) {
                    // ggf. multimediale Datei loeschen
                    val criteria = Criteria.where("filename").isEqualTo(kundeId)
                    val query = Query(criteria)
                    gridFsTemplate.delete(query)

                    // store() schliesst auch den Inputstream
                    gridFsTemplate.store(inputStream, kundeId, contentType)
                            .toHexString()
                } else {
                    null
                }

                LOGGER.trace("ID GridFS: {}", idGridFS)
                idGridFS.toString().toMono()
            }

    private companion object {
        val LOGGER = getLogger()
        val TIMEOUT_SHORT = Duration.ofMillis(500)
    }
}
