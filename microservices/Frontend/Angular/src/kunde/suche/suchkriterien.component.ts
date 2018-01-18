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

import {Component, EventEmitter, Output} from '@angular/core'

import {fadeIn, log} from '../../shared'
import {Geschlecht} from '../shared/kunde'
import {KundeService} from '../shared/kunde.service'

/**
 * Komponente f&uuml;r das Tag <code>hs-suchkriterien</code>
 */
@Component({
    selector: 'hs-suchkriterien',
    templateUrl: './suchkriterien.html',
    animations: [fadeIn],
})
export default class SuchkriterienComponent {
    vorname: string|undefined
    nachname: string|undefined
    ort: string|undefined
    geschlecht: Geschlecht|undefined
    /*familienstand: familienstandArt|undefined
    S = false
    L = false
    R = false*/

    // Event Binding: <hs-suchkriterien (waiting)="...">
    // Observables = Event-Streaming mit Promises
    @Output() waiting: EventEmitter<boolean> = new EventEmitter<boolean>()

    // DI: Constructor Injection (React hat uebrigens keine DI)
    // Empfehlung: Konstruktor nur fuer DI
    constructor(private readonly kundeService: KundeService) {
        console.log('SuchkriterienComponent.constructor()')
    }

    /**
     * Suche nach Kunden, die den spezfizierten Suchkriterien entsprechen
     * @param suchkriterien: Suchkriterien vom Typ IKundeForm
     * @return false, um das durch den Button-Klick ausgel&ouml;ste Ereignis
     *         zu konsumieren.
     */
    @log
    onFind() {
        const suchkriterien: any = {
            vorname: this.vorname,
            nachname: this.nachname,
            geschlecht: this.geschlecht,
            ort: this.ort,
        }
        console.log('suchkriterien=', suchkriterien)

        // Observables = Event-Streaming mit Promises
        this.waiting.emit(true)
        this.kundeService.find(suchkriterien)

        // Inspektion der Komponente mit dem Tag-Namen "app" im Debugger
        // Voraussetzung: globale Variable ng deklarieren (s.o.)
        // const app: any = document.querySelector('app')
        // global.ng.probe(app)

        // damit das (Submit-) Ereignis konsumiert wird und nicht an
        // uebergeordnete Eltern-Komponenten propagiert wird bis zum
        // Refresh der gesamten Seite.
        return false
    }

    toString() {
        return 'SuchkriterienComponent'
    }
}
