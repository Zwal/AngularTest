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

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
internal open class UsernameExistsException (username: String) :
        RuntimeException("Der Username $username existiert bereits")

/**
 * Zu werfende Exception, falls der Benutzername oder das Passwort nicht
 * korrekt ist. Diese Exception ist mit dem HTTP-Statuscode 401 gekoppelt.
 * Der Konstruktor kapselt aus Sicherheitsgrnden keine Fehlermeldung.
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
internal class NotAuthenticatedException : UsernameNotFoundException("")
