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

// Pattern "Page Objects" http://martinfowler.com/bliki/PageObject.html

const login = function(username, password) {
    // wegen this kann keine "Arrow Function"" benutzt werden

    // s. globale Konstante waitForConditionTimeout in nightwatch.js
    // ggf. noch ergaenzen:   .before(millisek)

    const {expect} = this
    expect.element('@username').to.be.visible
    expect.element('@password').to.be.visible
    expect.element('@loginButton').to.be.visible
    expect.element('@loginButton').text.to.equal('Login')

    return this.clearValue('@username')
        .setValue('@username', username)
        .clearValue('@password')
        .setValue('@password', password)
        .click('@loginButton')
        // Alternative zu Mausklick: Formular abschicken
        // .submitForm('@form')
}

const checkLogin = function() {
    const {expect} = this
    expect.element('@logoutIcon').to.be.visible
    expect.element('@logoutButton').text.to.contain('Logout')
    expect.element('@username').to.be.not.present
    expect.element('@password').to.be.not.present
    expect.element('@loginButton').to.be.not.present
    return this
}

const logout = function() {
    return this.click('@logoutIcon')
}

const checkLogout = function() {
    const {expect} = this
    expect.element('@username').to.be.visible
    expect.element('@password').to.be.visible
    expect.element('@loginButton').to.be.visible
    expect.element('@loginButton').text.to.equal('Login')
    expect.element('@logoutButton').to.be.not.present
    return this
}

export default {
    url: 'https://localhost',

    elements: {
        form: {
            selector: 'hs-auth form',
        },
        username: {
            selector: 'input[id=usernameInput]',
        },
        password: {
            selector: 'input[id=passwordInput]',
        },
        loginButton: {
            selector: 'hs-auth button[class="btn btn-primary btn-sm ml-3"]',
        },
        logoutIcon: {
            selector: 'hs-auth button i[class="fa fa-sign-out"]',
        },
        logoutButton: {
            selector: 'hs-auth button[class="btn btn-primary"]',
        },
    },

    // "Shorthand Property" fuer die Kommandos, s.o.
    commands: [
        {
            login,
            checkLogin,
            logout,
            checkLogout,
        },
    ],
}
