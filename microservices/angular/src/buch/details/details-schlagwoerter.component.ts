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

import {Component, Input, OnInit} from '@angular/core'

import {log} from '../../shared'

/**
 * Komponente f&uuml;r das Tag <code>hs-schlagwoerter</code>
 */
@Component({
    selector: 'hs-details-schlagwoerter',
    template: `
        <div class="form-group row" *ngFor="let schlagwort of values">
            <div class="col col-11">
                <div class="checkbox">
                    <label [ngSwitch]="schlagwort">
                        <input type="checkbox" checked disabled class="checkbox">
                        <span *ngSwitchCase="'JAVASCRIPT'">JavaScript</span>
                        <span *ngSwitchCase="'TYPESCRIPT'">TypeScript</span>
                    </label>
                <div>
            </div>
        </div>
    `,
})
export default class DetailsSchlagwoerterComponent implements OnInit {
    // <hs-schlagwoerter [values]="buch.schlagwoerter">
    // Decorator fuer ein Attribut. Siehe InputMetadata
    @Input() values: Array<string>

    constructor() {
        console.log('DetailsSchlagwoerterComponent.constructor()')
    }

    @log
    ngOnInit() {
        console.log('values=', this.values)
    }

    toString() {
        return 'DetailsSchlagwoerterComponent'
    }
}
