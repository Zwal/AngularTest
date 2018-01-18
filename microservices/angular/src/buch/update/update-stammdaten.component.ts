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
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms'
import {Router} from '@angular/router'

import {HOME_PATH} from '../../app/routes'
import {log} from '../../shared'
import {Buch} from '../shared'
import {BuchService} from '../shared/buch.service'

/**
 * Komponente f&uuml;r das Tag <code>hs-stammdaten</code>
 */
@Component({
    selector: 'hs-update-stammdaten',
    templateUrl: './update-stammdaten.html',
})
export default class UpdateStammdatenComponent implements OnInit {
    // <hs-update-stammdaten [buch]="...">
    @Input() buch: Buch

    form: FormGroup
    titel: FormControl
    art: FormControl
    verlag: FormControl
    rating: FormControl

    constructor(
        private readonly formBuilder: FormBuilder,
        private readonly buchService: BuchService,
        private readonly router: Router) {
        console.log('UpdateStammdatenComponent.constructor()')
    }

    /**
     * Das Formular als Gruppe von Controls initialisieren und mit den
     * Stammdaten des zu &auml;ndernden Buchs vorbelegen.
     */
    @log
    ngOnInit() {
        console.log('buch=', this.buch)

        // Definition und Vorbelegung der Eingabedaten
        this.titel = new FormControl(this.buch.titel, Validators.compose([
            Validators.required, Validators.minLength(2),
            Validators.pattern(/^\w.*$/),
        ]))
        this.art = new FormControl(this.buch.art, Validators.required)
        this.verlag = new FormControl(this.buch.verlag)
        this.rating = new FormControl(this.buch.rating)
        // this.datum = new Control(this.buch.datum.toISOString())

        this.form = this.formBuilder.group({
            // siehe formControlName innerhalb von @Component({template: ...})
            titel: this.titel,
            art: this.art,
            verlag: this.verlag,
            rating: this.rating,
            // datum: this.datum
        })
    }

    /**
     * Die aktuellen Stammdaten f&uuml;r das angezeigte Buch-Objekt
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

        if (this.buch === undefined) {
            console.error('buch === undefined')
            return
        }

        // rating, preis und rabatt koennen im Formular nicht geaendert werden
        this.buch.updateStammdaten(
            this.titel.value, this.art.value, this.verlag.value,
            this.rating.value, this.buch.datum, this.buch.preis,
            this.buch.rabatt)
        console.log('buch=', this.buch)

        const successFn = () => {
            console.log(`UpdateStammdaten: successFn: path: ${HOME_PATH}`)
            this.router.navigate([HOME_PATH])
        }
        const errFn: (status: number,
                      errors: {[s: string]: any}|undefined) => void =
            (status, errors = undefined) => {
            console.error(`UpdateStammdatenComponent.onUpdate(): errFn(): status: ${status}`)
            console.error('UpdateStammdatenComponent.onUpdate(): errFn(): errors', errors)
        }

        this.buchService.update(this.buch, successFn, errFn)

        // damit das (Submit-) Ereignis konsumiert wird und nicht an
        // uebergeordnete Eltern-Komponenten propagiert wird bis zum
        // Refresh der gesamten Seite
        return false
    }

    toString() {
        return 'UpdateStammdatenComponent'
    }
}
