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

import {Component, Input} from '@angular/core'

/**
 * Komponente f&uuml;r die Darstellung einer Fehlermeldung durch das Tag
 * &lt;hs-error-message [text]="..."&gt;
 */
@Component({
    selector: 'hs-error-message',
    template: `
        <div class="text-danger" *ngIf="text !== undefined">
            <i class="fa fa-exclamation-circle"></i>
            <span class="font-weight-bold">{{ text }}</span>
        </div>
    `,
})
export default class ErrorMessageComponent {
    // Property Binding: <hs-error-message [text]="...">
    // siehe InputMetadata
    @Input() text: string|undefined

    constructor() {
        console.log('ErrorMessageComponent.constructor()')
    }
}
