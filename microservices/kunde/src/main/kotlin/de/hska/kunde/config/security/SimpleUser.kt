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
package de.hska.kunde.config.security

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

/**
 * Entity-Klasse, um Benutzerkennungen bestehend aus Benutzername,
 * Passwort und Rollen zu reprsentieren, die in MongoDB verwaltet
 * werden.
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Document
internal class SimpleUser(
        val id: String?,
        username: String,
        password: String,
        authorities: Collection<SimpleGrantedAuthority> =
            listOf(SimpleGrantedAuthority("ROLE_KUNDE"))
) : User(username, password, authorities) {

    override fun toString() =
        "SimpleUser(super=${super.toString()}, id='$id')"

    /**
     * Konvertierungsklasse fuer MongoDB, um einen String einzulesen und
     * eine Rolle als GrantedAuthority zu erzeugen. Wegen @ReadingConverter
     * ist kein Lambda-Ausdruck moeglich.
     */
    @ReadingConverter
    class RoleReadConverter : Converter<String, GrantedAuthority> {
        override fun convert(role: String) = SimpleGrantedAuthority(role)
    }

    /**
     * Konvertierungsklasse fuer MongoDB, um eine Rolle (GrantedAuthority)
     * in einen String zu konvertieren. Wegen @WritingConverter ist kein
     * Lambda-Ausdruck moeglich.
     */
    @WritingConverter
    class RoleWriteConverter : Converter<GrantedAuthority, String> {
        override fun convert(grantedAuthority: GrantedAuthority) =
            grantedAuthority.authority
    }
}
