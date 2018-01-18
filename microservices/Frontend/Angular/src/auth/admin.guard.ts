/*
 * Copyright (C) 2016 Juergen Zimmermann, Hochschule Karlsruhe
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

import {Inject, Injectable} from '@angular/core'
import {ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot} from '@angular/router'
import {Observable} from 'rxjs/Observable'

// import {HOME_PATH} from '../app/routes'
import {log} from '../shared'

import {AuthService} from './auth.service'

// https://angular.io/docs/ts/latest/guide/router.html#can-activate-guard
// https://angular.io/docs/ts/latest/api/router/index/CanActivate-interface.html

@Injectable()
export class AdminGuard implements CanActivate {
    constructor(
        @Inject(AuthService) private authService: AuthService) {
        console.log('AdminGuard.constructor()')
    }

    @log
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot):
        Observable<boolean>|Promise<boolean>|boolean {
        if (this.authService.isAdmin()) {
            console.log('AdminGuard.canActivate(): admin')
            return true
        }

        console.warn('Nicht in der Rolle "admin" eingeloggt')
        return false
    }

    toString() {
        return 'AdminGuard'
    }
}
