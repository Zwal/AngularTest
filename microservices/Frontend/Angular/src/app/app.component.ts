/*
 * Copyright (C) 2015 - 2017 Juergen Zimmermann, Hochschule Karlsruhe
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

// Angular unterstuetzt npm als Package Manager. Deshalb werden .d.ts-Dateien
// im Verzeichnis node_modules bereitgestellt.
// https://github.com/angular/angular/issues/5248#issuecomment-156886060
// https://github.com/Microsoft/TypeScript/issues/5039
// https://github.com/DefinitelyTyped/DefinitelyTyped

// "core" enthaelt Funktionalitaet, damit die Webanwendung im Browser laeuft
import {Component} from '@angular/core'

// "Composite Pattern" bei UIs: Eine UI-Komponente besteht aus wiederum aus
// einfachen UI-Komponenten, z.B. ein Suchformular besteht aus einem Label,
// einem Eingabefeld und einem Button.

// Eine Komponente (= funktionale Einheit) ist an das MVC-Pattern angelehnt:
// sie besteht aus einem HTML-Template (= View) und der zugehoerigen
// Dialogsteuerung (= Controller) mit dem Model als Bindeglied.
// Controller sind klein ("Thin Controllers") und die Anwendungslogik wird
// in die Service-Klassen ausgelagert.
// Innerhalb der Wurzelkomponente werden die Kindkomponenten geladen.
// https://angular.io/docs/js/latest/api/annotations/ComponentAnnotation-class.html

// Metadaten-Annotationen in Angular sind z.B. @Component.
// Annotationen sind ein Spezialfall der Decorators, die es ab ES 7 geben wird:
// Ein Decorator *ergaenzt* die vorhandene Funktionalitaet von einer Klasse oder
// einer Methode oder einem Attribut oder einem Methodenargument.
// siehe https://github.com/wycats/javascript-decorators

// Generierter Code einschl. IIFE durch einen Transpiler z.B.:
// var App = (function() {
//     function App() {/* Konstruktor */}
//     ...
//     App = __decorate([
//         angular2_1.Component({
//             selector: 'app',
//             template: "..."
//         }),
//         __metadata('design:paramtypes', [])
//     ], App)
//     return App
// })()

/**
 * Wurzelkomponente mit dem Tag &lt;hs-app&gt;
 */
@Component({
    // Schnittstelle der View fuer Wiederverwendung in anderen Komponenten:
    // durch das Tag <hs-app> in index.html, d.h. CSS-Selector wird spezifiziert
    // Schreibweise innerhalb von HTML:         kebab-case
    // Schreibweise innerhalb von TypeScript:   CamelCase
    // Beispiel:
    //   <hs-app>
    //       <hs-header>
    //           ...
    //       </hs-header>
    //       <hs-main>
    //           <router-outlet>
    //               <hs-suche-kunden>
    //                   <hs-suchformular>
    //                       ...
    //                   </hs-suchformular>
    //                   <hs-suchergebnis>
    //                       ...
    //                   </hs-suchergebnis>
    //               </hs-suche-kunden>
    //           <router-outlet>
    //       </hs-main>
    //   </hs-app>
    selector: 'hs-app',

    // "template - A document or file having a preset format, used as a
    // starting point for a particular application so that the format does not
    // have to be recreated each time it is used."
    // Siehe http://www.thefreedictionary.com/template
    // HTML-Templates ~ View bei MVC: das Model referenzieren u. den Controller
    // aufrufen.
    // Multi-line Strings fuer kleine Inline-Templates.
    // Vorteile:  alles auf einen Blick und keine separate HTML-Datei
    // Nachteile: kein Syntax-Highlighting, kein Autovervollstaendigen
    // VS Code soll kuenftig Syntax-Highlighting und IntelliSense koennen:
    // https://github.com/angular/angular/blob/master/CHANGELOG.md#features-4
    //
    // Composed DOM: Der Baum und die Tags, die im Browser dargestellt werden
    // Light DOM:    Der Baum, in den der Shadow-DOM eingefuegt wird,
    //               z.B. <suche>
    // Shadow DOM:   Der Baum, der innerhalb des Light DOM eingefuegt wird,
    //               z.B. das Template aus SucheTitel.
    //               Dieser Baum ist zunaechst vor dem Endbenutzer verborgen
    // http://webcomponents.org/polyfills/shadow-dom
    // http://w3c.github.io/webcomponents/spec/shadow
    // https://github.com/angular/angular/issues/2529

    template: `
        <!-- Eigene Komponente fuer die Navigationsleiste -->
        <hs-header></hs-header>

        <!-- CSS-Klasse von Bootstrap
                http://getbootstrap.com/css
                http://www.w3schools.com/bootstrap
                https://scotch.io/bar-talk/whats-new-in-bootstrap-4
                https://www.npmjs.com/package/@ng-bootstrap/ng-bootstrap
                http://codingthesmartway.com/using-bootstrap-with-angular -->
        <!-- Eigene Komponente fuer den Haupteil: austauschbar durch Routing -->
        <hs-main></hs-main>

        <hs-footer></hs-footer>
    `,
    // encapsulation: ViewEncapsulation.Native
})

// Definitionsklasse ~ Controller: Eingabedaten entgegennehmen, Model fuer die
// View aktualisieren, Funktionen fuer die Benutzer-Interaktion bereitstellen,
// z.B. onClick oder onSubmit
// Die Doku zu Angular haengt immer noch den Suffix "Component" an die Klasse
// Eine Komponentenklasse braucht trotz default-Export einen Klassennamen, damit
// sie als Direktive injiziert werden kann.
export default class AppComponent {
    constructor() {
        console.log('AppComponent.constructor()')
    }
}
