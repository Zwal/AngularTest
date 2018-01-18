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

// In Anlehnung an:
// https://medium.com/front-end-hacking/javascript-make-your-code-cleaner-with-decorators-d34fc72af947
// http://html5hive.org/getting-started-with-angular-2#crayon-560cd5f774dd7156114609
// https://github.com/wycats/javascript-decorators
// https://github.com/mgechev/aspect.js

/**
 * Decorator f&uuml;r eine Methode, um in der Konsole folgendes zu
 * protokollieren:
 * <ul>
 *  <li> Methodenaufruf:&nbsp;&nbsp;&nbsp; &gt;
 *       <i>Klassenname</i>.<i>Methodenname</i>; zzgl. aktuelle Argumente
 *  <li> Methodenende:&nbsp;&nbsp;&nbsp; &lt;
 *       <i>Klassenname</i>.<i>Methodenname</i> zzgl. R&uuml;ckgabewert
 * </ul>
 */
export function log(
        target: any,
        key: string|symbol,
        descriptor: TypedPropertyDescriptor<any>):
    TypedPropertyDescriptor<any> {
    'use strict'
    const originalMethod = descriptor.value

    // keine Arrow Function wg. this im Funktionsrumpf; Spread-Operator
    // tslint:disable-next-line:only-arrow-functions
    descriptor.value = function(...args: Array<any>) {
        const klasseAsString = target.toString()
        // indexOf: Zaehlung ab 0. -1 bedeutet nicht enthalten
        // bei Klassen mit toString() werden ggf. Attributwerte nach einem ":""
        // ausgegeben
        const positionColon = klasseAsString.indexOf(':')
        const klassenname = positionColon === -1 ?
            klasseAsString :
            klasseAsString.substring(0, positionColon)

        if (args.length === 0) {
            console.log(`> ${klassenname}.${key}()`)
        } else {
            console.log(`> ${klassenname}.${key}():`, args)
        }

        // tslint:disable-next-line:no-invalid-this
        const result = originalMethod.apply(this, args)

        const resultStr = `< ${klassenname}.${key}(): result =`
        if (result === undefined) {
            console.log(`${resultStr} void || undefined`)
        } else {
            console.log(resultStr, result)
        }

        return result
    }
    return descriptor
}
