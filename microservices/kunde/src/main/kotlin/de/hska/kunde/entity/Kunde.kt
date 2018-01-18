/*
 * Copyright (C) 2013 - 2017 Juergen Zimmermann, Hochschule Karlsruhe
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
package de.hska.kunde.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import de.hska.kunde.config.security.SimpleUser
import de.hska.kunde.rest.util.ItemLinks
import de.hska.kunde.rest.util.SingleLinks
import java.net.URL
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Past
import javax.validation.constraints.Pattern
import org.springframework.data.annotation.Transient
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Daten eines Kunden.
 * In DDD: Kunde ist ein "Aggregate Root".
 * <img src="../../../../../images/Kunde.png" alt="Klassendiagramm"></img>
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Document
@JsonPropertyOrder(
        "nachname", "email", "kategorie", "newsletter", "geburtsdatum",
        "umsatz", "homepage", "geschlecht", "familienstand", "interessen",
        "adresse", "user")
internal data class Kunde (
    @get:Pattern(regexp = ID_PATTERN, message = "{kunde.id.pattern}")
    @JsonIgnore
    val id: String?,

    @get:NotEmpty(message = "{kunde.nachname.notEmpty}")
    @get:Pattern(
        regexp = NACHNAME_PATTERN,
        message = "{kunde.nachname.pattern}")
    @Indexed
    val nachname: String,

    @get:NotEmpty(message = "{kunde.email.notEmpty}")
    @get:Email(message = "{kunde.email.pattern}")
    @Indexed(unique = true)
    val email: String,

    @get:Min(value = MIN_KATEGORIE, message = "{kunde.kategorie.min}")
    @get:Max(value = MAX_KATEGORIE, message = "{kunde.kategorie.max}")
    val kategorie: Int = 0,

    val newsletter: Boolean = false,

    @get:Past(message = "{kunde.geburtsdatum.past}")
    val geburtsdatum: LocalDate?,

    // "sparse" statt NULL bei relationalen DBen
    // Keine Indizierung der Kunden, bei denen es kein solches Feld gibt
    @Indexed(sparse = true)
    val umsatz: Umsatz? = null,

    val homepage: URL? = null,

    val geschlecht: GeschlechtType?,

    val familienstand: FamilienstandType? = null,

    val interessen: List<InteresseType>?,

    @get:Valid
    @get:NotNull(message = "{kunde.adresse.notNull}")
    // @DBRef fuer eine eigenstaendige Collection
    //  auch fuer 1:N-Beziehunge, d.h. Attribute vom Typ List, Set, ...
    //  kein kaskadierendes save(), ...
    val adresse: Adresse,

    @Indexed(unique = true)
    val username: String? = null

// Caveat: Basisklasse darf keine data-Klasse sein
// https://discuss.kotlinlang.org/t/data-class-inheritance/4107/2
) : Auditable() {

    // Transiente Properties nicht im Konstruktor fuer Spring Data
    // var nicht val, da keine Initialisierung im Konstruktor
    @Transient
    var _links: SingleLinks? = null

    @Transient
    var links: ItemLinks? = null

    @Transient
    var user: SimpleUser? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Kunde
        return email == other.email
    }

    override fun hashCode() = email.hashCode()
    override fun toString() =
        "Kunde(id=$id, nachname=$nachname, email=$email, " +
        "kategorie=$kategorie, newsletter=$newsletter, " +
        "geburtsdatum=$geburtsdatum, umsatz=$umsatz, homepage=$homepage, " +
        "geschlecht=$geschlecht, familienstand=$familienstand, " +
        "interessen=$interessen, adresse=$adresse, username=$username, " +
        "_links=$_links, links=$links, user=$user)"

    companion object {
        private const val HEX_PATTERN = "[\\dA-Fa-f]"
        const val ID_PATTERN =
            "$HEX_PATTERN{8}-$HEX_PATTERN{4}-$HEX_PATTERN{4}-$HEX_PATTERN{4}-" +
                "$HEX_PATTERN{12}"

        private const val NACHNAME_PREFIX = "o'|von|von der|von und zu|van"
        private const val NAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+"
        const val NACHNAME_PATTERN =
                "($NACHNAME_PREFIX)?$NAME_PATTERN(-$NAME_PATTERN)?"

        const val MIN_KATEGORIE = 0L
        const val MAX_KATEGORIE = 9L
    }
}
