/*
 * Copyright (C) 2015 - 2016 Juergen Zimmermann, Hochschule Karlsruhe
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

import {Component, OnInit} from '@angular/core'  // externes Modul
import {Title} from '@angular/platform-browser'

import {log} from '../../shared'

/**
 * Komponente f&uuml;r das Tag <code>&lt;hs-suche-buecher&gt;</code>, die aus
 * den Kindkomponenten f&uuml;r diese Tags besteht:
 * <ul>
 *  <li> <code>hs-suchkriterien</code>
 *  <li> <code>hs-suchergebnis</code>
 * </ul>
 */
@Component({
    selector: 'hs-suche-kunden',
    template: `
        <hs-suchkriterien (waiting)="setWaiting($event)"></hs-suchkriterien>
        <hs-suchergebnis [waiting]="waiting"></hs-suchergebnis>
    `,
})
export default class SucheKundenComponent implements OnInit {
    waiting: boolean

    constructor(private readonly titleService: Title) {
        console.log('SucheKundenComponent.constructor()')
    }

    @log
    ngOnInit() {
        this.titleService.setTitle('Suche')
    }

    /**
     * Das Attribut <code>waiting</code> wird auf den Wert des boole'schen
     * Ereignisses <code>$event</code> gesetzt. Diese Methode wird aufgerufen,
     * wenn in der Kindkomponente f&uuml;r <code>hs-suchformular</code> das
     * Ereignis ausgel&ouml;st wird. Der aktuelle Wert vom Attribut
     * <code>&lt;waiting&gt;</code> wird an die Kindkomponente f&uuml;r
     * <code>&lt;suchergebnis&gt;</code> weitergereicht.
     * @param $event true f&uuml;r Aktivieren von WaitingComponent
     */
    @log
    setWaiting($event: boolean) {
        this.waiting = $event
    }

    toString() {
        return 'SucheBuecherComponent'
    }
}
