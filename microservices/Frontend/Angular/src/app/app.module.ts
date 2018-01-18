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

import {NgModule} from '@angular/core'
import {BrowserModule} from '@angular/platform-browser'

import {AUTH_PROVIDERS} from '../auth/auth.module'
import HomeModule from '../home/home.module'
import KundeModule from '../kunde/kunde.module'
import LayoutModule from '../layout/layout.module'
import SharedModule from '../shared/shared.module'

import AppComponent from './app.component'
import ROUTES from './routes'

// https://angular.io/docs/ts/latest/guide/ngmodule.html
// https://angular.io/docs/ts/latest/cookbook/ngmodule-faq.html
// http://blog.angular-university.io/angular2-ngmodule

// "Application Root Module" (= Einstiegsmodul):
// Der Name ist per Konvention "AppModule"
// Ein Modul enthaelt logisch zusammengehoerige Funktionalitaet
@NgModule({
    // Von den importierten Modulen sind alle exportierten Komponenten nutzbar
    // Ein Modul muss die Module importieren, von denen es Funktionalitaet nutzt
    imports: [
        // Nur das Root Modul importiert BrowserModule
        BrowserModule, SharedModule, HomeModule, KundeModule, LayoutModule,
        AUTH_PROVIDERS, ROUTES,
    ],

    // Eigene Komponenten des Moduls
    // Jede nutzbare Komponente muss in genau 1 Modul deklariert sein
    declarations: [
        // Eigentliche Komponente
        AppComponent,
    ],

    // Nur das Rootmodul hat die Property "bootstrap", um die
    // Einstiegskomponente zu deklarieren
    bootstrap: [AppComponent],
})
export default class AppModule {
}
