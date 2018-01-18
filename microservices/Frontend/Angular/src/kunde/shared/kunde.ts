// tslint:disable:max-file-line-count

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

// import * as _ from 'lodash'
import * as moment from 'moment'
import 'moment/locale/de'

moment.locale('de')

// const MIN_RATING = 0
// const MAX_RATING = 5
export declare type Geschlecht = 'M' | 'W'
export declare type familienstandArt = 'L' | 'VH' | 'G' | 'VW'
// export declare type Adresse = { "strasse": "blabla", "ort": "blabla",}

/**
 * Gemeinsame Datenfelder unabh&auml;ngig, ob die Kundedaten von einem Server
 * (z.B. RESTful Web Service) oder von einem Formular kommen.
 */
export interface Adresse {
    plz: string,
    ort: string
}

export interface User {
    username: string,
    password: string
 }
export interface KundeShared {
    _id?: string|undefined
    nachname?: string|undefined
    vorname?: string|undefined
    email: string|undefined
    newsletter: boolean|undefined
    geburtsdatum: Date|undefined
    geschlecht?: Geschlecht|undefined
    familienstand: familienstandArt|undefined
    adresse: Adresse|undefined
    user: User|undefined
    username: string|undefined
    homepage: string|undefined
    links: Array<any>|undefined
}

/**
 * Daten vom und zum REST-Server:
 * <ul>
 *  <li> Arrays f&uuml;r mehrere Werte, die in einem Formular als Checkbox
 *       dargestellt werden.
 *  <li> Daten mit Zahlen als Datentyp, die in einem Formular nur als
 *       String handhabbar sind.
 * </ul>
 */
export interface KundeServer extends KundeShared {
    kategorie: number|undefined
    interessen?: Array<string>|undefined
}

/**
 * Daten aus einem Formular:
 * <ul>
 *  <li> je 1 Control fuer jede Checkbox und
 *  <li> au&szlig;erdem Strings f&uuml;r Eingabefelder f&uuml;r Zahlen.
 * </ul>
 */
export interface KundeForm extends KundeShared {
    plz: string
    ort: string
    strasse: string
    kategorie: number|undefined
    username: string
    password: string
    S?: boolean
    L?: boolean
    R?: boolean
}

/**
 * Model als Plain-Old-JavaScript-Object (POJO) fuer die Daten *UND*
 * Functions fuer Abfragen und Aenderungen.
 */
export class Kunde {

        // wird aufgerufen von fromServer() oder von fromForm()
        private constructor(
            // tslint:disable-next-line:variable-name
            public _id: string|undefined,
            public links: Array<any>|undefined,
            public nachname: string|undefined,
            public vorname: string|undefined,
            public email: string|undefined,
            public kategorie: number|undefined,
            public newsletter: boolean|undefined,
            public geburtsdatum: Date|undefined,
            public homepage: string|undefined,
            public geschlecht: Geschlecht|undefined,
            public familienstand: familienstandArt|undefined,
            public interessen: Array<string>|undefined,
            public adresse: Adresse|undefined,
            public user: User|undefined,
            public username: string|undefined) {
            // this._id = _id || undefined
            this.links = links || undefined
            this.nachname = nachname || undefined
            this.vorname = vorname || undefined
            this.email = email || undefined
            this.kategorie = kategorie || undefined
            this.newsletter = newsletter || undefined
            this.geburtsdatum = geburtsdatum || undefined
            this.homepage = homepage || undefined
            this.geschlecht = geschlecht || undefined
            this.familienstand = familienstand || undefined
            if (interessen === undefined) {
                this.interessen = []
            } else {
                const tmpInteressen = interessen as Array<string>
                this.interessen = tmpInteressen
            }
            this.adresse = adresse || undefined
            this.user = user || undefined
            this.username = username || undefined

            if (this.links !== undefined) {
            let idString = this.links[1].href
            idString = idString.split('/', 4)
            this._id = idString[3]
            }
        }

    /**
     * Ein Kunde-Objekt mit JSON-Daten erzeugen, die von einem RESTful Web
     * Service kommen.
     * @param kunde JSON-Objekt mit Daten vom RESTful Web Server
     * @return Das initialisierte Kunden-Objekt
     */
    static fromServer(kundeServer: KundeServer) {
        let idString
        console.error(`Link im fromServer ${kundeServer.links}`)
        if (kundeServer.links !== undefined) {
            idString = kundeServer.links[1].href
            idString = idString.split('/', 4)
            idString = idString[3]}
        console.error(`ID aus dem Limk im fromServer ${idString}`)
        console.error(`ID im fromServer ${kundeServer._id}`)
        const kunde = new Kunde(
            kundeServer._id, kundeServer.links, kundeServer.nachname, kundeServer.vorname, kundeServer.email,
            kundeServer.kategorie, kundeServer.newsletter, kundeServer.geburtsdatum,
            kundeServer.homepage, kundeServer.geschlecht, kundeServer.familienstand,
            kundeServer.interessen, kundeServer.adresse, kundeServer.user, kundeServer.username)
        console.log('Kunde.fromServer(): kunde=', kunde)
        return kunde
    }

    /**
     * Ein Kunde-Objekt mit JSON-Daten erzeugen, die von einem Formular kommen.
     * @param kunde JSON-Objekt mit Daten vom Formular
     * @return Das initialisierte Kunde-Objekt
     */
    static fromForm(kundeForm: KundeForm) {
        const interessen: Array<string> = []
        if (kundeForm.S) {
            interessen.push('S')
        }
        if (kundeForm.L) {
            interessen.push('L')
        }
        if (kundeForm.R) {
            interessen.push('R')
        }

        const user: User = {username: kundeForm.username, password: kundeForm.password}
        const adresse: Adresse = { plz: kundeForm.plz, ort: kundeForm.ort }

//        const rabatt = kundeForm.rabatt === undefined ? 0 : kundeForm.rabatt / 100
        const kunde = new Kunde(
            kundeForm._id, kundeForm.links, kundeForm.nachname, kundeForm.vorname, kundeForm.email, kundeForm.kategorie,
            kundeForm.newsletter, kundeForm.geburtsdatum, kundeForm.homepage, kundeForm.geschlecht,
            kundeForm.familienstand, interessen, adresse, user, kundeForm.username)
        console.log('Kunde.fromForm(): kunde=', kunde)
        return kunde
    }

    /**
     * Abfrage, ob im Nachnamen der angegebene Teilstring enthalten ist. Dabei
     * wird nicht auf Gross-/Kleinschreibung geachtet.
     * @param nachname Zu &uuml;berpr&uuml;fender Teilstring
     * @return true, falls der Teilstring im Nachnamen enthalten ist. Sonst
     *         false.
     */
    containsTitel(nachname: string) {
        let result = true
        if (this.nachname !== undefined) {
            const tmp = this.nachname as string
            result = tmp.toLowerCase().includes(nachname.toLowerCase())
        }
        return result
    }

    // /**
    //  * Den Umsatz ("umsatz") des Kunden um 1 erh&ouml;hen
    //  */
    // umsatzUp() {
    //     if (this.umsatz !== undefined && this.umsatz.betrag < MAX_RATING) {
    //         this.umsatz.betrag++
    //     }
    // }

    // /**
    //  * Den Umsatz ("umsatz") des Kunden um 1 erniedrigen
    //  */
    // umsatzDown() {
    //     if (this.umsatz !== undefined && this.umsatz.betrag > MIN_RATING) {
    //         this.umsatz.betrag--
    //     }
    // }

    /**
     * Abfrage, ob der Kunde dem angegebenen Geschlecht zugeordnet ist.
     * @param geschlecht der Name des Geschlechts
     * @return true, falls der Kunde dem Geschlecht zugeordnet ist. Sonst false.
     */
    hasGeschlecht(geschlecht: string) {
        return this.geschlecht === geschlecht
    }

    /**
     * Aktualisierung der Stammdaten des Kunde-Objekts.
     * @param nachname Der neue Nachname
     * @param vorname Der neue Nachname
     * @param umsatz Der neue Umsatz
     * @param familienstand Der neue Familienstand (L, VH, G oder VW)
     * @param geschlecht Das neue Geschlecht
     */
    updateStammdaten(
        nachname: string, vorname: string, familienstand: familienstandArt, geschlecht: Geschlecht,
        betrag: number) {
        this.nachname = nachname
        this.vorname = vorname
        this.familienstand = familienstand
        this.geschlecht = geschlecht
        // this.umsatzArray = []
        // _.times(umsatz.betrag - MIN_RATING, () => this.umsatzArray.push(true))
    }

    /**
     * Abfrage, ob es zum Kunde auch Interessen gibt.
     * @return true, falls es mindestens eine Interesse gibt. Sonst false.
     */
    hasInteressen() {
        if (this.interessen === undefined) {
            return false
        }
        const tmpInteressen = this.interessen as Array<string>
        return tmpInteressen.length !== 0
    }

    /**
     * Abfrage, ob es zum Kunde das angegebene Interesse gibt.
     * @param interesse das zu &uuml;berpr&uuml;fende Interesse
     * @return true, falls es die Interesse gibt. Sonst false.
     */
    hasInteresse(interesse: string) {
        if (this.interessen === undefined) {
            return false
        }
        const tmpInteressen = this.interessen as Array<string>
        return tmpInteressen.includes(interesse)
    }

    /**
     * Aktualisierung der Interessen des Kunde-Objekts.
     * @param S ist die Interesse SPORT gesetzt
     * @param L ist die Interesse LESEN gesetzt
     * @param R ist die Interesse REISEN gesetzt
     */
    updateInteressen(SPORT: boolean, LESEN: boolean, REISEN: boolean) {
        this.resetInteressen()
        if (SPORT) {
            this.addInteresse('S')
        }
        if (LESEN) {
            this.addInteresse('L')
        }
        if (REISEN) {
            this.addInteresse('R')
        }
    }

    /**
     * Konvertierung des Kundeobjektes in ein JSON-Objekt f&uuml;r den RESTful
     * Web Service.
     * @return Das JSON-Objekt f&uuml;r den RESTful Web Service
     */
    toJSON(): KundeServer {
        console.error(`toJSON ID ${this._id}`)
        return {
            _id: this._id,
            links: this.links,
            nachname: this.nachname,
            vorname: this.vorname,
            email: this.email,
            kategorie: this.kategorie,
            newsletter: this.newsletter,
            geburtsdatum: this.geburtsdatum,
            homepage: this.homepage,
            geschlecht: this.geschlecht,
            familienstand: this.familienstand,
            interessen: this.interessen,
            adresse: this.adresse,
            user: this.user,
            username: this.username,
        }
    }

    toString() {
        return JSON.stringify(this, null, 2)
    }

    private resetInteressen() {
        this.interessen = []
    }

    private addInteresse(interesse: string) {
        if (this.interessen === undefined) {
            this.interessen = []
        }
        const tmpInteressen = this.interessen as Array<string>
        tmpInteressen.push(interesse)
    }
}
