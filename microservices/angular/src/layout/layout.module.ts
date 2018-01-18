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

import {NgModule, Type} from '@angular/core'

import ROUTES from '../app/routes'
import SharedModule from '../shared/shared.module'

import AuthComponent from './auth.component'
import FooterComponent from './footer.component'
import HeaderComponent from './header.component'
import LogoComponent from './logo.component'
import MainComponent from './main.component'
import NavComponent from './nav.component'

const komponentenExport: Array<Type<any>> = [
    AuthComponent, FooterComponent, HeaderComponent, LogoComponent,
    MainComponent, NavComponent,
]
const komponentenIntern: Array<Type<any>> = []

@NgModule({
    imports: [SharedModule, ROUTES],
    declarations: [...komponentenExport, ...komponentenIntern],
    exports: komponentenExport,
})
export default class LayoutModule {
}
