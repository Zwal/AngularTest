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

import {Component, Input, OnInit} from '@angular/core'
import {FormBuilder, FormControl, FormGroup} from '@angular/forms'
import {Router} from '@angular/router'

import {HOME_PATH} from '../../app/routes'
import {log} from '../../shared'
import {Kunde} from '../shared'
import {KundeService} from '../shared/kunde.service'

/**
 * Komponente f&uuml;r das Tag <code>hs-interessen</code>
 */
@Component({
    selector: 'hs-update-interessen',
    templateUrl: './update-interessen.html',
})
export default class UpdateInteressenComponent implements OnInit {
    // <hs-interessen [kunde]="...">
    @Input() kunde: Kunde

    form: FormGroup
    S: FormControl
    L: FormControl
    R: FormControl

    constructor(
        private readonly formBuilder: FormBuilder,
        private readonly kundeService: KundeService,
        private readonly router: Router) {
        console.log('UpdateInteressenComponent.constructor()')
    }

    /**
     * Das Formular als Gruppe von Controls initialisieren und mit den
     * Interessen des zu &auml;ndernden Kunden vorbelegen.
     */
    @log
    ngOnInit() {
        console.log('kunde=', this.kunde)

        // Definition und Vorbelegung der Eingabedaten (hier: Checkbox)
        const hasSport = this.kunde.hasInteresse('SPORT')
        this.S = new FormControl(hasSport)
        const hasLesen = this.kunde.hasInteresse('LESEN')
        this.L = new FormControl(hasLesen)
        const hasReisen = this.kunde.hasInteresse('REISEN')
        this.R = new FormControl(hasReisen)

        this.form = this.formBuilder.group({
            // siehe ngFormControl innerhalb von @Component({template: `...`})
            S: this.S,
            L: this.L,
            R: this.R,
        })
    }

    /**
     * Die aktuellen Interessen f&uuml;r das angezeigte Kunde-Objekt
     * zur&uuml;ckschreiben.
     * @return false, um das durch den Button-Klick ausgel&ouml;ste Ereignis
     *         zu konsumieren.
     */
    @log
    onUpdate() {
        if (this.form.pristine) {
            console.log('keine Aenderungen')
            return
        }

        if (this.kunde === undefined) {
            console.error('kunde === undefined/null')
            return
        }

        this.kunde.updateInteressen(
            this.S.value, this.L.value, this.R.value)
        console.log('kunde=', this.kunde)

        const successFn = () => {
            console.log(
                `UpdateInteressenComponent: successFn: path: ${HOME_PATH}`)
            this.router.navigate([HOME_PATH])
        }
        const errFn: (status: number,
                      errors: {[s: string]: any}|undefined) => void =
            (status, errors = undefined) => {
            console.error(`UpdateInteressenComponent.onUpdate(): errFn(): status: ${status}`)
            console.error('UpdateInteressenComponent.onUpdate(): errFn(): errors', errors)
        }
        this.kundeService.update(this.kunde, successFn, errFn)

        // damit das (Submit-) Ereignis konsumiert wird und nicht an
        // uebergeordnete Eltern-Komponenten propagiert wird bis zum
        // Refresh der gesamten Seite
        return false
    }

    toString() {
        return 'UpdateInteressenComponent'
    }
}
