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

import {AbstractControl} from '@angular/forms'

export const emailValidator: (control: AbstractControl) => {[key: string]: true}|null =
    (control: AbstractControl) => {
        const {value} = control
        if (value === undefined || value === null || value === '') {
            return null
        }

        // http://tools.ietf.org/html/rfc22
        // http://www.w3.org/TR/html5/forms.html#valid-e-mail-address
        // tslint:disable:max-line-length
        const invalid =
            value.match(
                /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/)
            === null
        return invalid ? {invalidEmail: true} : null
    }
