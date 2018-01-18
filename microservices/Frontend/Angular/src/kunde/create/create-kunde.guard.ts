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

import {Injectable} from '@angular/core'
import {ActivatedRouteSnapshot, CanDeactivate, RouterStateSnapshot} from '@angular/router'

import {log} from '../../shared'

import CreateKundeComponent from './create-kunde.component'

@Injectable()
export default class CreateKundeGuard implements
    CanDeactivate<CreateKundeComponent> {
    constructor() {
        console.log('CreateKundeGuard.constructor()')
    }

    @log
    canDeactivate(
        createKunde: CreateKundeComponent, route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot) {
        if (createKunde.fertig) {
            return true
        }

        createKunde.showWarning = true
        createKunde.fertig = true
        console.warn('Beim Verlassen der Seite werden Daten verloren.')
        return false
    }

    toString() {
        return 'CreateKundeGuard'
    }
}
