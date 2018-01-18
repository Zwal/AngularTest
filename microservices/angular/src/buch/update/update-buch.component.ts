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
import {Buch} from '../shared'
import {BuchService} from '../shared/buch.service'

/**
 * Komponente f&uuml;r das Tag <code>hs-update-buch</code> mit Kindkomponenten
 * f&uuml;r die folgenden Tags:
 * <ul>
 *  <li> <code>hs-stammdaten</code>
 *  <li> <code>hs-schlagwoerter</code>
 * </ul>
 */
@Component({
    selector: 'hs-update-buch',
    templateUrl: './update-buch.html',
})
export default class UpdateBuchComponent implements OnInit {
    buch: Buch|undefined
    errorMsg: string|undefined

    constructor(
        private readonly buchService: BuchService,
        private readonly titleService: Title,
        private readonly route: ActivatedRoute) {
        console.log('UpdateBuchComponent.constructor()')
    }

    @log
    ngOnInit() {
        // Die Beobachtung starten, ob es ein zu aktualisierendes Buch oder
        // einen Fehler gibt.
        this.observeBuch()
        this.observeError()

        // Pfad-Parameter aus /update/:id
        const next: (params: Params) => void = params => {
            console.log('params=', params)
            this.buchService.findById(params.id)
        }

        // ActivatedRoute.params is an Observable
        this.route.params.subscribe(next)
        this.titleService.setTitle('Aktualisieren')
    }

    toString() {
        return 'UpdateBuchComponent'
    }

    /**
     * Beobachten, ob es ein zu aktualisierendes Buch gibt.
     */
    private observeBuch() {
        const next: (buch: Buch) => void = buch => {
            this.errorMsg = undefined
            this.buch = buch
            console.log('UpdateBuch.buch=', this.buch)
        }

        this.buchService.observeBuch(next)
    }

    /**
     * Beobachten, ob es einen Fehler gibt.
     */
    private observeError() {
        const next: (err: string|number) => void = err => {
            this.buch = undefined

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
                    this.errorMsg = 'Kein Buch vorhanden.'
                    break
                default:
                    this.errorMsg = 'Ein Fehler ist aufgetreten.'
                    break
            }
            console.log(`UpdateBuchComponent.errorMsg: ${this.errorMsg}`)
        }

        this.buchService.observeError(next)
    }
}
