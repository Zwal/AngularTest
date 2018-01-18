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
import {ActivatedRoute, Params} from '@angular/router'

import {isString, log} from '../../shared'
import {Kunde} from '../shared'
import {KundeService} from '../shared/kunde.service'

/**
 * Komponente f&uuml;r das Tag <code>hs-update-kunde</code> mit Kindkomponenten
 * f&uuml;r die folgenden Tags:
 * <ul>
 *  <li> <code>hs-stammdaten</code>
 *  <li> <code>hs-schlagwoerter</code>
 * </ul>
 */
@Component({
    selector: 'hs-update-kunde',
    templateUrl: './update-kunde.html',
})
export default class UpdateKundeComponent implements OnInit {
    kunde: Kunde|null = null
    errorMsg: string|null = null

    constructor(
        private readonly kundeService: KundeService,
        private readonly titleService: Title,
        private readonly route: ActivatedRoute) {
        console.log('UpdateKundeComponent.constructor()')
    }

    @log
    ngOnInit() {
        // Die Beobachtung starten, ob es ein zu aktualisierendes Kunde oder
        // einen Fehler gibt.
        this.observeKunde()
        this.observeError()

        // Pfad-Parameter aus /updateKunde/:id
        const next: (params: Params) => void = params => {
            console.log('params=', params)
            this.kundeService.findById(params.id)
        }

        // ActivatedRoute.params is an Observable
        this.route.params.subscribe(next)
        this.titleService.setTitle('Aktualisieren')
    }

    toString() {
        return 'UpdateKundeComponent'
    }

    /**
     * Beobachten, ob es ein zu aktualisierendes Kunde gibt.
     */
    private observeKunde() {
        const next: (kunde: Kunde) => void = kunde => {
            this.errorMsg = null
            this.kunde = kunde
            console.log('UpdateKunde.kunde=', this.kunde)
        }

        this.kundeService.observeKunde(next)
    }

    /**
     * Beobachten, ob es einen Fehler gibt.
     */
    private observeError() {
        const next: (err: string|number) => void = err => {
            this.kunde = null

            if (err === null) {
                this.errorMsg = 'Ein Fehler ist aufgetreten.'
                return
            }

            if (isString(err)) {
                this.errorMsg = err as string
                return
            }

            switch (err) {
                case 404:
                    this.errorMsg = 'Kein Kunde vorhanden.'
                    break
                default:
                    this.errorMsg = 'Ein Fehler ist aufgetreten.'
                    break
            }
            console.log(`UpdateKundeComponent.errorMsg: ${this.errorMsg}`)
        }

        this.kundeService.observeError(next)
    }
}
