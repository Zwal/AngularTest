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
import {Title} from '@angular/platform-browser'

import SharedModule from '../shared/shared.module'

import HomeComponent from './home.component'

const komponentenExport: Array<Type<any>> = [HomeComponent]
const komponentenIntern: Array<Type<any>> = []

@NgModule({
    // Der Singleton-Service "Title" wird benoetigt
    imports: [SharedModule.forRoot()],
    declarations: [...komponentenExport, ...komponentenIntern],
    providers: [Title],
    exports: komponentenExport,
})
export default class HomeModule {
}
