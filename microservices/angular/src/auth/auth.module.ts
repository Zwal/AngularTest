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

import {HTTP_INTERCEPTORS} from '@angular/common/http'
import {ModuleWithProviders, NgModule} from '@angular/core'

import {AdminGuard} from './admin.guard'
import {AuthInterceptor} from './auth.interceptor'
import {AuthService} from './auth.service'
import BasicAuthService from './basic-auth.service'
import CookieService from './cookie.service'

@NgModule({})
export default class AuthModule {
    // Export als Singleton im Root-Modul
    static forRoot(): ModuleWithProviders {
        return {
            ngModule: AuthModule,
            providers: [
                AdminGuard, AuthService, BasicAuthService, CookieService,
                [
                    {
                        provide: HTTP_INTERCEPTORS,
                        useClass: AuthInterceptor,
                        multi: true,
                    },
                ],
            ],
        }
    }
}

export const AUTH_PROVIDERS = AuthModule.forRoot()
