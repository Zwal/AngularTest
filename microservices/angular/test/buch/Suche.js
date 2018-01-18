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

import {PAUSE} from '../shared/constants'

export default {
    '@tags': ['buecher', 'suche'],

    after() {
        this.client.end()
    },

    afterEach(done) {
        this.client.pause(PAUSE)
        done()
    },

    'Suche alle Buecher'() {
        // Given
        this.client.page
            .suchePage()
            .navigate()

            // When
            .checkInit()
            .submit()

            // Then
            .checkAlleBuecher()
    },

    'Suche Buecher mit "a" im Titel'() {
        // Given
        const titel = 'a'
        this.client.page
            .suchePage()
            .navigate()

            // When
            .checkInit()
            .titel(titel)
            .submit()

            // Then
            .checkGefundeneBuecher(titel)
    },

    'Suche mit Fehlermeldung'() {
        // Given
        const titel = 'XXX'

        // When
        this.client.page
            .suchePage()
            .navigate()
            .titel(titel)
            .submit()

            // Then
            .checkFehlermeldung()
    },
}
