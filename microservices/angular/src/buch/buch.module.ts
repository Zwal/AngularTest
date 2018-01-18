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

import ROOT_ROUTES from '../app/routes'
import SharedModule from '../shared/shared.module'

import CreateBuchComponent from './create/create-buch.component'
import CreateBuchGuard from './create/create-buch.guard'
import DetailsBuchComponent from './details/details-buch.component'
import DetailsSchlagwoerterComponent from './details/details-schlagwoerter.component'
import DetailsStammdatenComponent from './details/details-stammdaten.component'
import BalkendiagrammComponent from './diagramme/balkendiagramm.component'
import LiniendiagrammComponent from './diagramme/liniendiagramm.component'
import TortendiagrammComponent from './diagramme/tortendiagramm.component'
import {BuchService} from './shared/buch.service'
import SucheBuecherComponent from './suche/suche-buecher.component'
import SuchergebnisComponent from './suche/suchergebnis.component'
import SuchformularComponent from './suche/suchformular.component'
import UpdateBuchComponent from './update/update-buch.component'
import UpdateSchlagwoerterComponent from './update/update-schlagwoerter.component'
import UpdateStammdatenComponent from './update/update-stammdaten.component'

const komponentenExport: Array<Type<any>> = [
    CreateBuchComponent, DetailsBuchComponent, BalkendiagrammComponent,
    LiniendiagrammComponent, TortendiagrammComponent, SucheBuecherComponent,
    UpdateBuchComponent,
]

const komponentenIntern: Array<Type<any>> = [
    DetailsSchlagwoerterComponent, DetailsStammdatenComponent,
    SucheBuecherComponent, SuchergebnisComponent, SuchformularComponent,
    UpdateSchlagwoerterComponent, UpdateStammdatenComponent,
]

// Ein Modul enthaelt logisch zusammengehoerige Funktionalitaet.
// Exportierte Komponenten koennen bei einem importierenden Modul in dessen
// Komponenten innerhalb deren Templates (= HTML-Fragmente) genutzt werden.
// BuchModule ist ein "FeatureModule", das Features fuer Buecher bereitstellt
@NgModule({
    imports: [SharedModule, SharedModule.forRoot(), ROOT_ROUTES],
    declarations: [...komponentenExport, ...komponentenIntern],
    // BuchService mit eigenem DI-Context innerhalb des Moduls, d.h.
    // es kann in anderen Moduln eine eigene Instanz von BuchService geben.
    // Title als Singleton aus dem SharedModule
    providers: [BuchService, CreateBuchGuard, Title],
    exports: komponentenExport,
})
export default class BuchModule {
}
