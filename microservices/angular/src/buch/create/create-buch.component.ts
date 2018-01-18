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

import {Component, OnInit} from '@angular/core'
// Bereitgestellt durch das ReactiveFormsModule (s. Re-Export im SharedModule)
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms'
import {Title} from '@angular/platform-browser'
// Bereitgestellt durch das RouterModule (s. Re-Export im SharedModule)
import {Router} from '@angular/router'

import {HOME_PATH} from '../../app/routes'
import {log} from '../../shared'
import {Buch, emailValidator} from '../shared'
import {BuchService} from '../shared/buch.service'

/**
 * Komponente mit dem Tag &lt;create-buch&gt;, um das Erfassungsformular
 * f&uuml;r ein neues Buch zu realisieren.
 */
@Component({
    // moduleId: module.id,
    selector: 'hs-create-buch',
    templateUrl: './create-buch.html',
})
export default class CreateBuchComponent implements OnInit {
    form: FormGroup

    // Keine Vorbelegung bzw. der leere String, da es Placeholder gibt
    // Varianten fuer Validierung:
    //    serverseitig mittels Request/Response
    //    clientseitig bei den Ereignissen keyup, change, ...
    // Ein Endbenutzer bewirkt staendig einen neuen Fehlerstatus
    readonly titel: FormControl = new FormControl(undefined, Validators.compose([
        Validators.required, Validators.minLength(2),
        Validators.pattern(/^\w.*$/),
    ]))
    readonly rating: FormControl = new FormControl(undefined)
    readonly art: FormControl = new FormControl('DRUCKAUSGABE')
    readonly verlag: FormControl = new FormControl(undefined)
    readonly preis: FormControl = new FormControl(undefined)
    readonly rabatt: FormControl = new FormControl(undefined)
    readonly datum: FormControl = new FormControl(undefined)
    readonly lieferbar: FormControl = new FormControl(false)
    readonly javascript: FormControl = new FormControl(false)
    readonly typescript: FormControl = new FormControl(false)
    readonly email: FormControl =
        new FormControl(undefined, [Validators.required, emailValidator] as any)

    showWarning = false
    fertig = false

    constructor(
        private formBuilder: FormBuilder,
        private buchService: BuchService, private router: Router,
        private titleService: Title) {
        console.log('CreateBuchComponent.constructor()')
        if (router !== undefined) {
            console.log('Injizierter Router:', router)
        }
    }

    /**
     * Das Formular als Gruppe von Controls initialisieren.
     */
    @log
    ngOnInit() {
        this.form = this.formBuilder.group({
            // siehe formControlName innerhalb @Component({template: ...})
            titel: this.titel,
            rating: this.rating,
            art: this.art,
            verlag: this.verlag,
            preis: this.preis,
            rabatt: this.rabatt,
            datum: this.datum,
            lieferbar: this.lieferbar,
            javascript: this.javascript,
            typescript: this.typescript,
            email: this.email,
        })

        this.titleService.setTitle('Neues Buch')
    }

    /**
     * Die Methode <code>save</code> realisiert den Event-Handler, wenn das
     * Formular abgeschickt wird, um ein neues Buch anzulegen.
     * @return false, um das durch den Button-Klick ausgel&ouml;ste Ereignis
     *         zu konsumieren.
     */
    @log
    onSave() {
        // In einem Control oder in einer FormGroup gibt es u.a. folgende
        // Properties
        //    value     JSON-Objekt mit den IDs aus der FormGroup als
        //              Schluessel und den zugehoerigen Werten
        //    errors    Map<string,any> mit den Fehlern, z.B. {'required': true}
        //    valid     true/false
        //    dirty     true/false, falls der Wert geaendert wurde

        if (!this.form.valid) {
            console.log('Validierungsfehler:', this.form)
            return false
        }

        const neuesBuch = Buch.fromForm(this.form.value)
        console.log('neuesBuch=', neuesBuch)

        const successFn: (location: string|undefined) => void =
            (location = undefined) => {
                console.log(
                    `CreateBuch.onSave(): successFn(): location: ${location}`)
                // TODO Das Response-Objekt enthaelt im Header NICHT "Location"
                console.log(
                    `CreateBuch.onSave(): successFn(): navigate: ${HOME_PATH}`)
                this.fertig = true
                this.showWarning = false
                this.router.navigate([HOME_PATH])
            }
        const errorFn: (status: number,
                        errors: {[s: string]: any}|undefined) => void =
            (status, errors) => {
                console.error(`CreateBuch.onSave(): errorFn(): status: ${status}`)
                console.error('CreateBuch.onSave(): errorFn(): errors', errors)
            }
        this.buchService.save(neuesBuch, successFn, errorFn)

        // damit das (Submit-) Ereignis konsumiert wird und nicht an
        // uebergeordnete Eltern-Komponenten propagiert wird bis zum Refresh
        // der gesamten Seite
        return false
    }

    toString() {
        return 'CreateBuchComponent'
    }
}
