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
package de.hska.kunde.entity

import com.fasterxml.jackson.annotation.JsonValue
import java.util.Locale
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

/**
 * Demozweck: Dropdown-Menue auf Clientseite
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Suppress("UseDataClass")
internal enum class FamilienstandType (val value: String) {
    LEDIG("L"),
    VERHEIRATET("VH"),
    GESCHIEDEN("G"),
    VERWITWET("VW");

    // https://github.com/FasterXML/jackson-databind/wiki
    @JsonValue
    override fun toString() = value

    /**
     * Konvertierungsklasse fuer MongoDB, um einen String einzulesen und
     * ein Enum-Objekt von FamilienstandType zu erzeugen.
     * Wegen @ReadingConverter ist kein Lambda-Ausdruck moeglich.
     */
    @ReadingConverter
    class ReadConverter : Converter<String, FamilienstandType> {
        override fun convert(value: String) = FamilienstandType.build(value)
    }

    /**
     * Konvertierungsklasse fuer MongoDB, um FamilienstandType in einen
     * String zu konvertieren.
     * Wegen @WritingConverter ist kein Lambda-Ausdruck moeglich.
     */
    @WritingConverter
    class WriteConverter : Converter<FamilienstandType, String> {
        override fun convert(familienstand: FamilienstandType) =
            familienstand.value
    }

    companion object {
        private val LOCALE_DEFAULT = Locale.getDefault()
        private val NAME_CACHE = HashMap<String, FamilienstandType>()
        init {
            for (familienstand in enumValues<FamilienstandType>()) {
                NAME_CACHE.put(familienstand.value, familienstand)
                NAME_CACHE.put(familienstand.value.toLowerCase(LOCALE_DEFAULT),
                        familienstand)
                NAME_CACHE.put(familienstand.name, familienstand)
                NAME_CACHE.put(familienstand.name.toLowerCase(LOCALE_DEFAULT),
                        familienstand)
            }
        }

        /**
         * Konvertierung eines Strings in einen Enum-Wert
         * @param value Der String, zu dem ein passender Enum-Wert ermittelt
         * werden soll.
         * Keine Unterscheidung zwischen Gross- und Kleinschreibung.
         * @return Passender Enum-Wert oder null
         */
        fun build(value: String?) = NAME_CACHE[value]
    }
}
