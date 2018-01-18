/*
 * Copyright (C) 2016 Juergen Zimmermann, Hochschule Karlsruhe
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

import {Injectable} from '@angular/core'

// NICHT: Session-Cookies mit serverseitiger Session-ID.

// Namen der Cookies: nur als Speichermechanismus (nicht zum Server übertragen):
// Ablaufdatum oder Session-Cookie (Lebensdauer gebunden an Tab).
// Kein XSS (Cross-Site Scripting) wie bei Local Storage
// Evtl. CSRF (Cross-Site Request Forgery)
// Besser: Session-Cookies mit dem Token

const AUTHORIZATION = 'authorization'
const ROLES = 'roles'

@Injectable()
export default class CookieService {
    constructor() {
        console.log('CookieService.constructor()')
    }

    saveAuthorization(authorization: string, roles: string, expiration: number) {
        this.setCookie(AUTHORIZATION, authorization, expiration)
        this.setCookie(ROLES, roles, expiration)
    }

    getAuthorization() {
        return this.getCookie(AUTHORIZATION)
    }

    getRoles() {
        return this.getCookie(ROLES)
    }

    deleteAuthorization() {
        this.deleteCookie(AUTHORIZATION)
        this.deleteCookie(ROLES)
    }

    toString() {
        return 'CookieService'
    }

    // In Anlehnung an
    // https://github.com/BCJTI/ng2-cookies/blob/master/src/services/cookie.ts

    /**
     * @param name Name des gesuchten Cookies
     * @return Werte des gefundenes Cookie oder undefined
     */
    private getCookie(name: string) {
        name = encodeURIComponent(name)
        const regexp = new RegExp(`(?:^${name}|;\\s*${name})=(.*?)(?:;|$)`, 'g')
        // alle Cookies durchsuchen
        const result = regexp.exec(document.cookie)
        // z.B. %20 durch Leerzeichen ersetzen
        return (result === null) ? undefined : decodeURIComponent(result[1])
    }

    /**
     * @param name Name des Cookies
     * @param value Wert des Cookies
     * @param expires Ablaufdatum in Millisekunden. Default: Session.
     * @param path Pfad des Cookies. Default: /.
     * @param domain Domain des Cookies. Default: aktuelle Domain.
     */
    private setCookie(
        name: string, value: string, expires?: number, path?: string,
        domain?: string) {
        let cookieStr =
            `${encodeURIComponent(name)}=${encodeURIComponent(value)};`

        if (expires !== undefined) {
            const expirationDate = new Date(expires as number)
            cookieStr += `expires=${expirationDate.toUTCString()};`
        }
        if (path !== undefined) {
            cookieStr += `path=${path};`
        }
        if (domain !== undefined) {
            cookieStr += `domain=${domain};`
        }
        // Kein Zugriff mit JavaScript; Uebertragung nur mit HTTPS
        // cookieStr += 'HttpOnly; Secure;'

        // Uebertragung nur mit HTTPS
        cookieStr += 'Secure;'

        console.log(`setCookie(): ${cookieStr}`)
        // neues Cookie anlegen
        document.cookie = cookieStr
    }

    /**
     * @param name Name des Cookies
     * @param path Pfad des Cookies. Default: /.
     * @param domain Domain des Cookies. Default: aktuelle Domain.
     */
    private deleteCookie(name: string, path?: string, domain?: string) {
        if (this.getCookie(name)) {
            // expires in der Vergangenheit
            this.setCookie(name, '', -1, path, domain)
        }
    }
}
