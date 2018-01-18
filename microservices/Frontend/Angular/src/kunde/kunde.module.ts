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

import CreateKundeComponent from './create/create-kunde.component'
import CreateKundeGuard from './create/create-kunde.guard'
import DetailsInteressenComponent from './details/details-interessen.component'
import DetailsKundeComponent from './details/details-kunde.component'
import DetailsStammdatenComponent from './details/details-stammdaten.component'
import BalkendiagrammComponent from './diagramme/balkendiagramm.component'
import LiniendiagrammComponent from './diagramme/liniendiagramm.component'
import TortendiagrammComponent from './diagramme/tortendiagramm.component'
import {KundeService} from './shared/kunde.service'
import SucheKundenComponent from './suche/suche-kunden.component'
import SuchergebnisComponent from './suche/suchergebnis.component'
import SuchkriterienComponent from './suche/suchkriterien.component'
import UpdateInteressenComponent from './update/update-interessen.component'
import UpdateKundeComponent from './update/update-kunde.component'
import UpdateStammdatenComponent from './update/update-stammdaten.component'

const komponentenExport: Array<Type<any>> = [
    CreateKundeComponent, DetailsKundeComponent, BalkendiagrammComponent,
    LiniendiagrammComponent, TortendiagrammComponent, SucheKundenComponent,
    UpdateKundeComponent,
]

const komponentenIntern: Array<Type<any>> = [
    DetailsInteressenComponent, DetailsStammdatenComponent,
    SucheKundenComponent, SuchergebnisComponent, SuchkriterienComponent,
    UpdateInteressenComponent, UpdateStammdatenComponent,
]

// Ein Modul enthaelt logisch zusammengehoerige Funktionalitaet.
// Exportierte Komponenten koennen bei einem importierenden Modul in dessen
// Komponenten innerhalb deren Templates (= HTML-Fragmente) genutzt werden.
// KundeModule ist ein "FeatureModule", das Features fuer Kunden bereitstellt
@NgModule({
    imports: [SharedModule, SharedModule.forRoot(), ROOT_ROUTES],
    declarations: [...komponentenExport, ...komponentenIntern],
    // KundeService mit eigenem DI-Context innerhalb des Moduls, d.h.
    // es kann in anderen Moduln eine eigene Instanz von KundeService geben.
    // Title als Singleton aus dem SharedModule
    providers: [KundeService, CreateKundeGuard, Title],
    exports: komponentenExport,
})
export default class KundeModule {
}
