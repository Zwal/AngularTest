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

// http://nightwatchjs.org/guide/#windows-
import 'nightwatch/bin/runner.js'

// Selenium ermoeglicht Tests mit Chrome, FireFox und Edge, erfordert aber einen
// zusaetzlichen Selenium-Server zzgl. Treiber fuer die jeweiligen Browser.

// ChromeDriver (s. nightwatch.conf.js) implementiert das "W3C WebDriver wire protocol"
// fuer Chrome, erfordert aber "External Globals" fuer Tests
// http://nightwatchjs.org/gettingstarted/#standalone-usage
// http://nightwatchjs.org/guide/#external-globals
import * as chromedriver from 'chromedriver'

// Globaler Timeout in Millisekunden
export const waitForConditionTimeout = 5000

export const before = done => {
    chromedriver.start()
    done()
}

export const after = done => {
    chromedriver.stop()
    done()
}
