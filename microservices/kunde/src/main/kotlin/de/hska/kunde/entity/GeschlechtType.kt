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
package de.hska.kunde.entity

import com.fasterxml.jackson.annotation.JsonValue
import java.util.Locale
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

/**
 * Demozweck: Radiobuttons auf Clientseite
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Suppress("UseDataClass")
internal enum class GeschlechtType (val value: String) {
    MAENNLICH("M"),
    WEIBLICH("W");

    // https://github.com/FasterXML/jackson-databind/wiki
    @JsonValue
    override fun toString() = value

    /**
     * Konvertierungsklasse fuer MongoDB, um einen String einzulesen und
     * ein Enum-Objekt von GeschlechtType zu erzeugen. Wegen @ReadingConverter
     * ist kein Lambda-Ausdruck moeglich.
     */
    @ReadingConverter
    class ReadConverter : Converter<String, GeschlechtType> {
        override fun convert(value: String) = GeschlechtType.build(value)
    }

    /**
     * Konvertierungsklasse fuer MongoDB, um GeschlechtType in einen String
     * zu konvertieren. Wegen @WritingConverter ist kein Lambda-Ausdruck
     * moeglich.
     */
    @WritingConverter
    class WriteConverter : Converter<GeschlechtType, String> {
        override fun convert(geschlecht: GeschlechtType) = geschlecht.value
    }

    companion object {
        private val LOCALE_DEFAULT = Locale.getDefault()
        private val NAME_CACHE = HashMap<String, GeschlechtType>()
        init {
            for (geschlecht in enumValues<GeschlechtType>()) {
                NAME_CACHE.put(geschlecht.value, geschlecht)
                NAME_CACHE.put(geschlecht.value.toLowerCase(LOCALE_DEFAULT),
                        geschlecht)
                NAME_CACHE.put(geschlecht.name, geschlecht)
                NAME_CACHE.put(geschlecht.name.toLowerCase(LOCALE_DEFAULT),
                        geschlecht)
            }
        }

        /**
         * Konvertierung eines Strings in einen Enum-Wert
         * @param value Der String, zu dem ein passender Enum-Wert ermittelt
         * werden soll.
         * Keine Unterscheidung zwischen Gross- und Kleinschreibung.
         * @return Passender Enum-Wert oder WEIBLICH
         */
        fun build(value: String?) = NAME_CACHE[value]
    }
}
