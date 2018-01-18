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
import {Title} from '@angular/platform-browser'
// Bereitgestellt durch das RouterModule (s. Re-Export im SharedModule)
import {ActivatedRoute, Params} from '@angular/router'

import {AuthService, ROLLE_ADMIN} from '../../auth/auth.service'
import {isString, log} from '../../shared'
import {Kunde} from '../shared'
import {KundeService} from '../shared/kunde.service'

/**
 * Komponente f&uuml;r das Tag <code>hs-details-kunde</code>
 */
@Component({
    selector: 'hs-details-kunde',
    templateUrl: './details-kunde.html',
})
export default class DetailsKundeComponent implements OnInit {
    waiting = false
    kunde: Kunde|null = null
    errorMsg: string|null = null
    isAdmin: boolean

    constructor(
        private kundeService: KundeService, private titleService: Title,
        private route: ActivatedRoute, private authService: AuthService) {
        console.log('DetailsKundeComponent.constructor()')
    }

    @log
    ngOnInit() {
        // Die Beobachtung starten, ob es ein zu darzustellendes Kunde oder
        // einen Fehler gibt.
        this.observeKunde()
        this.observeError()

        // Pfad-Parameter aus /details/:id
        // Mongo-ID ist ein String
        const next: (params: Params) => void = params => {
            console.log('params=', params)
            this.kundeService.findById(params.id)
            console.error(`details-kunde id ? ${params.id}`)
        }
        // ActivatedRoute.params ist ein Observable
        this.route.params.subscribe(next)

        // Initialisierung, falls zwischenzeitlich der Browser geschlossen wurde
        this.isAdmin = this.authService.isAdmin()
        this.observeIsAdmin()
    }

    toString() {
        return 'DetailsKundeComponent'
    }

    private observeKunde() {
        const next: (kunde: Kunde) => void = kunde => {
            this.waiting = false
            this.kunde = kunde
            console.log('DetailsKundeComponent.kunde=', this.kunde)

            const titel =
                this.kunde === undefined ? 'Details' : `Details ${this.kunde._id}`
            this.titleService.setTitle(titel)
        }
        this.kundeService.observeKunde(next)
    }

    private observeError() {
        const next: (err: string|number|undefined) => void = err => {
            this.waiting = false
            if (err === undefined) {
                this.errorMsg = 'Ein Fehler ist aufgetreten.'
                return
            }

            if (isString(err)) {
                this.errorMsg = err as string
                return
            }

            switch (err) {
                case 404:
                    this.errorMsg = 'Kein Kunde gefunden.'
                    break
                default:
                    this.errorMsg = 'Ein Fehler ist aufgetreten.'
                    break
            }
            console.log(`DetailsKundeComponent.errorMsg: ${this.errorMsg}`)

            this.titleService.setTitle('Fehler')
        }

        this.kundeService.observeError(next)
    }

    private observeIsAdmin() {
        const next: (event: Array<string>) => void = event => {
            this.isAdmin = event.includes(ROLLE_ADMIN)
            console.log('DetailsKundeComponent.isAdmin:', this.isAdmin)
        }
        this.authService.observeRollen(next)
    }
}
