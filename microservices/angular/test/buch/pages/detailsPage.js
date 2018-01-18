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

const checkTitel = function(titel) {
    const {expect} = this

    expect.element('@tabSchlagwoerter').to.be.visible
    expect.element('@tabStammdaten').to.be.visible

    expect.element('@tabelle').to.be.visible
    expect.element('@titel').text.to.be.equal(titel)
    return this
}

const clickUpdateButton = function() {
    const {expect, click} = this

    expect.element('@tabSchlagwoerter').to.be.visible
    expect.element('@tabStammdaten').to.be.visible

    expect.element('@updateButton').to.be.visible
    click('@updateButton')
    return this
}

const checkNoUpdateButton = function() {
    this.expect.element('@updateButton').to.be.not.present
    return this
}

export default {
    url: 'https://localhost/details',

    elements: {
        tabStammdaten: {
            selector: 'a[href="#stammdaten"]',
        },
        tabSchlagwoerter: {
            selector: 'a[href="#schlagwoerter"]',
        },
        tabelle: {
            selector: 'table[class="table table-striped table-hover ' +
                                   'table-responsive"] tbody',
        },
        titel: {
            selector: 'table[class="table table-striped table-hover ' +
                                   'table-responsive"] tbody ' +
                                   'tr:first-child td:nth-child(2)',
        },
        updateButton: {
            selector: 'a[title="Bearbeiten"] i[class="fa fa-2x fa-edit"]',
        },
    },

    commands: [
        {
            checkTitel,
            clickUpdateButton,
            checkNoUpdateButton,
        },
    ],
}
