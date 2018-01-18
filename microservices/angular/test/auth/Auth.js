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

import {ADMIN_PASSWORD, ADMIN_USERNAME, PAUSE} from '../shared/constants'

// Alternativen zu Nightwatch:
// * Nightwatch enthaelt auch Mocha
// * Katalon Studio
// * Appium

export default {
    '@tags': ['auth', 'login'],

    // "Shorthand Properties" ab ES 2015
    after() {
        this.client.end()
    },

    afterEach(done) {
        this.client.pause(PAUSE)
        done()
    },

    'Login mit gueltigen Daten'() {
        // Given
        const username = ADMIN_USERNAME
        const password = ADMIN_PASSWORD
        const {page} = this.client

        // When
        page.authPage()
            // URL des "Page Objects" aufrufen
            .navigate()
            // Kommando des "Page Objects" aufrufen
            .login(username, password)

        // Then
        page.authPage().checkLogin()
        page.authPage().logout().checkLogout()
    },

    // https://github.com/nightwatchjs/nightwatch-docs/blob/master/guide/running-tests/disabling-tests.md
    // http://nightwatchjs.org/guide/#test-tags
    // http://nightwatchjs.org/guide/#disabling-tests
    /* eslint no-empty-function: 0, prefer-template: 0 */
    'Login mit ungueltigen Daten': '' + function() {
    },
}
