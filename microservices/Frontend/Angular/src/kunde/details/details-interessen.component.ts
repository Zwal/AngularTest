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
 * Komponente f&uuml;r das Tag <code>hs-interessen</code>
 */
@Component({
    selector: 'hs-details-interessen',
    template: `
        <div class="form-group row" *ngFor="let interessen of values">
            <div class="col col-11 offset-1">
                <div class="checkbox">
                    <label [ngSwitch]="interessen">
                        <input type="checkbox" checked disabled class="checkbox">
                        <span *ngSwitchCase="'S'">Sport</span>
                        <span *ngSwitchCase="'L'">Lesen</span>
                        <span *ngSwitchCase="'R'">Reisen</span>
                    </label>
                <div>
            </div>
        </div>
    `,
})
export default class DetailsInteressenComponent implements OnInit {
    // <hs-interessen [values]="kunde.interessen">
    // Decorator fuer ein Attribut. Siehe InputMetadata
    @Input() values: Array<string>

    constructor() {
        console.log('DetailsInteressenComponent.constructor()')
    }

    @log
    ngOnInit() {
        console.log('values=', this.values)
    }

    toString() {
        return 'DetailsInteressenComponent'
    }
}
