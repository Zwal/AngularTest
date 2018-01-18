/*
 * Copyright (C) 2015 - 2017 Juergen Zimmermann, Hochschule Karlsruhe
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

import {Component, OnInit} from '@angular/core'
import {Router} from '@angular/router'

import {HOME_PATH} from '../app/routes'
import {AuthService} from '../auth/auth.service'
import {log} from '../shared'

/**
 * Komponente f&uuml;r das Login mit dem Tag &lt;hs-auth&gt;.
 */
@Component({
    selector: 'hs-auth',
    templateUrl: './auth.html',
})
export default class AuthComponent implements OnInit {
    username: string
    password: string
    notLoggedIn: boolean

    constructor(
        private readonly authService: AuthService,
        private readonly router: Router) {
        console.log('LoginComponent.constructor()')
    }

    @log
    ngOnInit() {
        // Initialisierung, falls zwischenzeitlich der Browser geschlossen wurde
        this.notLoggedIn = !this.authService.isLoggedIn()
        this.observeLogin()
    }

    @log
    onLogin() {
        this.authService.login(this.username, this.password)
    }

    /**
     * Ausloggen und dabei Benutzername und Passwort zur&uuml;cksetzen.
     */
    @log
    onLogout() {
        this.authService.logout()
        this.router.navigate([HOME_PATH])
    }

    toString() {
        return 'AuthComponent'
    }

    /**
     * Methode, um den injizierten <code>AuthService</code> zu beobachten,
     * ob es Login-Informationen gibt. Diese private Methode wird in der Methode
     * <code>ngOnInit</code> aufgerufen.
     */
    private observeLogin() {
        const next: (event: boolean) => void = event => {
            if (this.notLoggedIn && !event) {
                // Noch nicht eingeloggt und ein Login-Event kommt, d.h.
                // es gab einen Login-Versuch, der aber fehlerhaft (= false) war
                // TODO Anzeige des fehlgeschlagenen Logins
                console.warn('LoginComponent: Falsche Login-Daten')
            }
            this.notLoggedIn = !event
            console.log('LoginComponent.notLoggedIn:', this.notLoggedIn)
        }

        // Funktion als Funktionsargument, d.h. Code als Daten uebergeben
        this.authService.observeIsLoggedIn(next)
    }
}
