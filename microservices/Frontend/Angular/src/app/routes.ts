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

import {ModuleWithProviders} from '@angular/core'
import {RouterModule, Routes} from '@angular/router'

import CreateKundeComponent from '../kunde/create/create-kunde.component'
import CreateKundeGuard from '../kunde/create/create-kunde.guard'
import DetailsKundeComponent from '../kunde/details/details-kunde.component'
import BalkendiagrammComponent from '../kunde/diagramme/balkendiagramm.component'
import LiniendiagrammComponent from '../kunde/diagramme/liniendiagramm.component'
import TortendiagrammComponent from '../kunde/diagramme/tortendiagramm.component'
import SucheKundenComponent from '../kunde/suche/suche-kunden.component'
import UpdateKundeComponent from '../kunde/update/update-kunde.component'

// import {AdminGuard} from '../auth/admin.guard'
import HomeComponent from '../home/home.component'

export const HOME_PATH = 'home'
export const DETAILS_KUNDE_PATH = 'details'

// https://angular.io/docs/ts/latest/guide/router.html
/**
 * Route-Definitionen f&uuml;r AppModule.
 */
const routes: Routes = [
    {path: HOME_PATH, component: HomeComponent},
    {path: '', redirectTo: HOME_PATH, pathMatch: 'full'},

    {path: 'suche', component: SucheKundenComponent},
    // id als Pfad-Parameter
    {path: `${DETAILS_KUNDE_PATH}/:id`, component: DetailsKundeComponent},
    {
        path: 'update/:id',
        component: UpdateKundeComponent,
        // canActivate: [AdminGuard],
    },
    {
      path: 'create',
      component: CreateKundeComponent,
      // canActivate: [AdminGuard],
      canDeactivate: [CreateKundeGuard],
    },
    {
      path: 'balkendiagramm',
      component: BalkendiagrammComponent,
      // canActivate: [AdminGuard],
    },
    {
      path: 'liniendiagramm',
      component: LiniendiagrammComponent,
      // canActivate: [AdminGuard],
    },
    {
      path: 'tortendiagramm',
      component: TortendiagrammComponent,
      // canActivate: [AdminGuard],
    },

    // Weiterer Pfad fuer die Produktion.
    // In der Entwicklung ist es einfacher, bei FALSCHEN Pfaden die Fehler sehen
    // {path: '**', component: NotFoundComponent}
]

const ROUTES: ModuleWithProviders = RouterModule.forRoot(routes)
export default ROUTES
