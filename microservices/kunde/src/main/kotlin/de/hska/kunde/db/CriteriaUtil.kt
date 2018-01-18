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
package de.hska.kunde.db

import de.hska.kunde.entity.FamilienstandType
import de.hska.kunde.entity.GeschlechtType
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.CriteriaDefinition
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.util.MultiValueMap

internal object CriteriaUtil {
    private val NACHNAME = "nachname"
    private val EMAIL = "email"
    private val KATEGORIE = "kategorie"
    private val PLZ = "plz"
    private val PLZ_FIELD = "adresse.plz"
    private val ORT = "ort"
    private val ORT_FIELD = "adresse.ort"
    private val UMSATZ_MIN = "umsatzmin"
    private val UMSATZ = "umsatz.betrag"
    private val GESCHLECHT = "geschlecht"
    private val FAMILIENSTAND = "familienstand"
    private val INTERESSEN = "interessen"
    private val ANY_STR = "\\.*"
    private val LOGGER = getLogger()

    fun getCriteria(queryParams: MultiValueMap<String, String>
    ): List<CriteriaDefinition?> {
        val criteria = queryParams.map { (key, value) ->
            if (value?.size != 1) {
                null
            } else {
                val critVal = value[0]
                when (key) {
                    NACHNAME -> getCriteriaNachname(critVal)
                    EMAIL -> getCriteriaEmail(critVal)
                    KATEGORIE -> getCriteriaKategorie(critVal)
                    PLZ -> getCriteriaPlz(critVal)
                    ORT -> getCriteriaOrt(critVal)
                    UMSATZ_MIN -> getCriteriaUmsatz(critVal)
                    GESCHLECHT -> getCriteriaGeschlecht(critVal)
                    FAMILIENSTAND -> getCriteriaFamilienstand(critVal)
                    INTERESSEN -> getCriteriaInteressen(critVal)
                    else -> null
                }
            }
        }
        LOGGER.debug("#Criteria: {}", criteria.size)
        return criteria
    }

    private fun getCriteriaNachname(nachname: String): Criteria {
        LOGGER.trace("Nachname: {}", nachname)
        // Suche nach Teilstrings ohne Gross-/Kleinschreibung
        return Criteria.where(NACHNAME)
                .regex("$ANY_STR$nachname$ANY_STR", "i")
    }

    private fun getCriteriaEmail(email: String): Criteria {
        LOGGER.trace("Email: {}", email)
        // Suche ohne Gross-/Kleinschreibung
        return Criteria.where(EMAIL).regex(email, "i")
    }

    private fun getCriteriaKategorie(kategorieStr: String): Criteria? {
        LOGGER.trace("Kategorie: {}", kategorieStr)
        val kategorie = try {
            kategorieStr.toInt()
        } catch (e: NumberFormatException) {
            LOGGER.debug("Fehler bei der Kategorie: {}", e.message)
            null
        }

        return Criteria.where(KATEGORIE).isEqualTo(kategorie)
    }

    private fun getCriteriaPlz(plz: String): Criteria {
        LOGGER.trace("PLZ: {}", plz)
        // Suche mit Praefix
        return Criteria.where(PLZ_FIELD).regex("$plz$ANY_STR")
    }

    private fun getCriteriaOrt(ort: String): Criteria {
        LOGGER.trace("Ort: {}", ort)
        // Suche nach Teilstrings ohne Gross-/Kleinschreibung
        return Criteria.where(ORT_FIELD)
                .regex("$ANY_STR$ort${ANY_STR}i")
    }

    private fun getCriteriaUmsatz(umsatzStr: String): Criteria? {
        LOGGER.trace("Umsatz: {}", umsatzStr)
        val umsatz = try {
            umsatzStr.toBigDecimal()
        } catch (e: NumberFormatException) {
            LOGGER.debug("Fehler beim Umsatz: {}", e.message)
            return null
        }

        return Criteria.where(UMSATZ).gte(umsatz)
    }

    private fun getCriteriaGeschlecht(geschlechtStr: String): Criteria? {
        LOGGER.trace("Geschlecht: {}", geschlechtStr)
        val geschlecht = GeschlechtType.build(geschlechtStr)
        return if (geschlecht == null)
            null
        else
            Criteria.where(GESCHLECHT).isEqualTo(geschlecht)
    }

    private fun getCriteriaFamilienstand(familienstandStr: String): Criteria? {
        LOGGER.trace("Familienstand: {}", familienstandStr)
        val familienstand = FamilienstandType.build(familienstandStr)
        return if (familienstand == null)
            null
        else
            Criteria.where(FAMILIENSTAND).isEqualTo(familienstand)
    }

    private fun getCriteriaInteressen(interessenStr: String): Criteria? {
        LOGGER.trace("Interessen: {}", interessenStr)
        val interessenList = interessenStr
                .split(",".toRegex())
                .dropLastWhile { it.isEmpty() }
        if (interessenList.isEmpty()) {
            return null
        }

        // Interessen mit "and" verknuepfen, falls mehr als 1
        val criteria = interessenList.map {
            Criteria.where(INTERESSEN).isEqualTo(it)
        }.toMutableList()

        val firstCriteria = criteria[0]
        if (criteria.size == 1) {
            return firstCriteria
        }

        criteria.removeAt(0)
        @Suppress("SpreadOperator")
        return firstCriteria.andOperator(*criteria.toTypedArray())
    }
}
