/*
 * Copyright (C) 2016 - 2017 Juergen Zimmermann, Hochschule Karlsruhe
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

// Direktiven (z.B. ngFor, ngIf) und Pipes
import {CommonModule} from '@angular/common'
// HTTP-Methoden fuer REST
import {HttpClientModule} from '@angular/common/http'
// Kern-Funktionalitaet von Angular
import {ModuleWithProviders, NgModule, Type} from '@angular/core'
// Form Controls fuer statische und dynamische Formulare
import {FormsModule, ReactiveFormsModule} from '@angular/forms'
// Nachname im Tab aendern
import {Title} from '@angular/platform-browser'
// mind. 1x Animation
import {BrowserAnimationsModule} from '@angular/platform-browser/animations'

import DiagrammService from './diagramm.service'
import ErrorMessageComponent from './error-message.component'
import WaitingComponent from './waiting.component'

const komponentenReExport: Array<Type<any>> = [
        CommonModule, FormsModule, ReactiveFormsModule, HttpClientModule,
        BrowserAnimationsModule,
    ]
const komponentenExport: Array<Type<any>> =
    [ErrorMessageComponent, WaitingComponent]
const komponentenIntern: Array<Type<any>> = []

// Ein "Shared Module" stellt allgemeine Funktionalitaet fuer verschiedene
// "Feature Modules" bereit, wie z.B. KundeModule
@NgModule({
    // von den importierten Modulen sind deren exportierte Komponenten nutzbar
    imports: [...komponentenReExport],

    // Eigene Komponenten des Moduls
    // Jede nutzbare Komponente muss in genau 1 Modul *deklariert* sein
    declarations: [...komponentenExport, ...komponentenIntern],

    // diese Komponenten sind von anderen Modulen nutzbar, falls sie dieses
    // Modul importieren
    exports: [...komponentenReExport, ...komponentenExport],
})
export default class SharedModule {
    // Export als Singleton im Root-Modul
    static forRoot(): ModuleWithProviders {
        return {ngModule: SharedModule, providers: [Title, DiagrammService]}
    }
}
