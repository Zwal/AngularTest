/*
 * Copyright (C) 2017 Juergen Zimmermann
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

// jshint expr:true

import * as _ from 'lodash'

const checkInit = function() {
    const {expect} = this
    expect.element('@titel').to.be.visible
    return this
}

const titel = function(titelStr) {
    this.expect.element('@titel').to.be.visible
    return this.clearValue('@titel').setValue('@titel', titelStr)
}
const druckausgabe = function() {
    const {expect} = this
    expect.element('@verlag').to.be.visible
    this.click('@verlag')
    expect.element('@druckausgabe').to.be.visible
    return this.click('@druckausgabe')
}
const kindle = function() {
    const {expect} = this
    expect.element('@verlag').to.be.visible
    this.click('@verlag')
    expect.element('@kindle').to.be.visible
    return this.click('@kindle')
}
const javascript = function() {
    this.expect.element('@javascript').to.be.visible
    return this.click('@javascript')
}
const typescript = function() {
    this.expect.element('@typescript').to.be.visible
    return this.click('@typescript')
}
const submit = function() {
    this.expect.element('@button').to.be.visible
    return this.click('@button')
}
const checkAlleBuecher = function() {
    this.expect.element('@gefundeneBuecherHeader').to.be.visible
    return this
}

const checkGefundeneBuecher = function(titelSubstr) {
    this.expect.element('@gefundeneBuecherHeader').to.be.visible

    const {api} = this.client

    // Titel in der 1. Zeile ueberpruefen
    const selectorTitelZeile1 = 'hs-suchergebnis tbody tr:nth-child(1) ' +
                                'td:nth-child(3)'
    api.expect.element(selectorTitelZeile1).text.to.contain(titelSubstr)

    // von allen "n"" Zeilen die Spalte "3" mit dem Titel ermitteln:
    // Formel fuer nth-child():   a*n + b
    const selectorSpalte = 'hs-suchergebnis table tbody tr:nth-child(n) ' +
                           'td:nth-child(3)'

    // Callback zum Ueberpruefen des Titels in den Datenzellen der Spalte
    const checkTitel = zellen => {
        _.times(zellen.value.length, i => {
            /* eslint no-magic-numbers: 0, space-infix-ops: 0 */
            const selectorZelle = 'hs-suchergebnis tbody ' +
                                  `tr:nth-child(${i+1}) td:nth-child(3)`
            api.expect.element(selectorZelle).text.to.contain(titelSubstr)
        })
    }

    api.elements('css selector', selectorSpalte, checkTitel)

    this.expect.element('@fehlermeldung').to.be.not.present

    return this
}

const clickTitelErsteZeile = function() {
    this.expect.element('@titelErsteZeile').to.be.visible
    this.click('@titelErsteZeile')
}

const checkFehlermeldung = function() {
    const {expect} = this
    expect.element('@fehlermeldung').to.be.visible
    expect.element('@gefundeneBuecherHeader').to.be.not.present
    return this
}

const deleteErsteZeile = function() {
    this.expect.element('@deleteIconErsteZeile').to.be.visible
    return this.click('@deleteIconErsteZeile')
}

export default {
    url: 'https://localhost/suche',

    elements: {
        titel: {
            selector: 'input[id=nachnameInput]',
        },
        verlag: {
            selector: 'select[name=verlag]',
        },
        druckausgabe: {
            selector: 'input[value=DRUCKAUSGABE]',
        },
        kindle: {
            selector: 'input[value=KINDLE]',
        },
        javascript: {
            selector: 'input[name=javascript]',
        },
        typescript: {
            selector: 'input[name=typescript]',
        },
        button: {
            selector: 'button i[class="fa fa-search"]',
        },
        gefundeneBuecherHeader: {
            selector: 'div[class=card-header]',
        },
        fehlermeldung: {
            selector: 'div[class=text-danger] ' +
                      'i[class="fa fa-exclamation-circle"]',
        },
        titelErsteZeile: {
            selector: 'hs-suchergebnis ' +
                      'tbody tr:nth-child(1) td:nth-child(3)',
        },
        deleteIconErsteZeile: {
            selector: 'hs-suchergebnis ' +
                      'tbody tr:nth-child(1) td i[class="fa fa-remove"]',
        },
    },

    commands: [
        {
            checkInit,
            titel,
            druckausgabe,
            kindle,
            javascript,
            typescript,
            submit,
            checkAlleBuecher,
            checkGefundeneBuecher,
            checkFehlermeldung,
            clickTitelErsteZeile,
            deleteErsteZeile,
        },
    ],
}
