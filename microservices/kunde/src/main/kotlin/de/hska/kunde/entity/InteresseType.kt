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

import com.fasterxml.jackson.annotation.JsonValue
import java.util.Locale
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

/**
 * Demozweck: Checkboxen auf Clientseite
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Suppress("UseDataClass")
internal enum class InteresseType (val value: String) {
    SPORT("S"),
    LESEN("L"),
    REISEN("R");

    // https://github.com/FasterXML/jackson-databind/wiki
    @JsonValue
    override fun toString() = value

    /**
     * Konvertierungsklasse fuer MongoDB, um einen String einzulesen und
     * ein Enum-Objekt von InteresseType zu erzeugen. Wegen @ReadingConverter
     * ist kein Lambda-Ausdruck moeglich.
     */
    @ReadingConverter
    class ReadConverter : Converter<String, InteresseType> {
        override fun convert(value: String) = InteresseType.build(value)
    }

    /**
     * Konvertierungsklasse fuer MongoDB, um InteresseType in einen String
     * zu konvertieren. Wegen @WritingConverter ist kein Lambda-Ausdruck
     * mgoelich.
     */
    @WritingConverter
    class WriteConverter : Converter<InteresseType, String> {
        override fun convert(interesse: InteresseType) = interesse.value
    }

    companion object {
        private val LOCALE_DEFAULT = Locale.getDefault()
        private val NAME_CACHE = HashMap<String, InteresseType>()
        init {
            for (interesse in enumValues<InteresseType>()) {
                NAME_CACHE.put(interesse.value, interesse)
                NAME_CACHE.put(interesse.value.toLowerCase(LOCALE_DEFAULT),
                        interesse)
                NAME_CACHE.put(interesse.name, interesse)
                NAME_CACHE.put(interesse.name.toLowerCase(LOCALE_DEFAULT),
                        interesse)
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
