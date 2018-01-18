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

import {Inject, Injectable} from '@angular/core'

import {BASE_URI, log, TIMEZONE_OFFSET_MS} from '../shared'

import CookieService from './cookie.service'

@Injectable()
export default class JwtService {
    constructor(@Inject(CookieService) private readonly cookieService:
                    CookieService) {
        console.log('JwtService.constructor()')
    }

    // GET-Request durch fetch() von ES statt HttpClient von Angular
    @log
    async login(username: string, password: string):
        Promise<Array<string>|undefined> {
        console.log(`
            JwtService.login(): username=${username}, password=${password}`)
        const loginUri = `${BASE_URI}login`
        console.log(`Login URI = ${loginUri}`)

        // https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch
        const headers = new Headers()
        headers.append('Content-Type', 'application/x-www-form-urlencoded')
        const request = new Request(
            loginUri,
            {
                method: 'POST',
                headers,
                body: `username=${username}&password=${password}`,
            })

        let response: Response
        try {
            response = await fetch(request)
        // Optional catch binding parameters
        } catch {
            console.error(
                'JwtService.login: Kommunikationsfehler mit dem Appserver')
            return Promise.reject(
                new Error('Kommunikationsfehler mit dem Appserver'))
        }

        const {status} = response
        console.log(`status=${status}`)
        if (status !== 200) {
            return Promise.reject(new Error(response.statusText))
        }

        const json = await response.json()
        console.log('json', json)
        const {token, roles} = json
        const authorization = `Bearer ${token}`
        console.log(`authorization=${authorization}`)

        // Array von Strings als 1 String
        const rolesStr: string = roles.join()
        console.log(`rolesStr=${rolesStr}`)

        const decodedToken = this.decodeToken(token)
        console.log('decodedToken', decodedToken)
        if (decodedToken.exp === undefined) {
            return
        }

        // Expiration beim Token: Sekunden seit 1.1.1970 UTC
        // Cookie: Millisekunden in eigener Zeitzone
        const expiration = decodedToken.exp * 1000 + TIMEZONE_OFFSET_MS
        console.log(`fetch.then(): exp=${expiration}`)
        this.cookieService.saveAuthorization(
            authorization, rolesStr, expiration)

        return Promise.resolve(roles)
    }

    toString() {
        return 'JwtService'
    }

    // https://github.com/auth0/angular2-jwt/blob/master/angular2-jwt.ts#L147
    private decodeToken(token: string) {
        // Destructuring
        const [, payload, signature]: Array<string|undefined> =
            token.split('.')
        if (signature === undefined) {
            console.error('JWT enthaelt keine Signature')
            return undefined
        }

        let base64Token = payload.replace(/-/g, '+').replace(/_/g, '/')
        switch (base64Token.length % 4) {
            case 0:
                break
            case 2:
                base64Token += '=='
                break
            case 3:
                base64Token += '='
                break
            default:
                console.error('Laenge des JWT in Base64 ist falsch.')
                return undefined
        }

        // tslint:disable:max-line-length
        // http://xkr.us/articles/javascript/encode-compare
        // http://stackoverflow.com/questions/75980/when-are-you-supposed-to-use-escape-instead-of-encodeuri-encodeuricomponent#23842171
        // tslint:enable:max-line-length
        const decodedStr =
            decodeURIComponent(encodeURIComponent(window.atob(base64Token)))
        if (decodedStr === undefined) {
            console.error('JWT kann nicht decodiert werden.')
            return undefined
        }
        return JSON.parse(decodedStr)
    }
}
