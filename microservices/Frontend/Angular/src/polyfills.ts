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

// Polyfills fuer ES 5, ES 2016, ES 2017, ...
import 'core-js/es6'
import 'core-js/es7/reflect'

import 'zone.js/dist/zone'

if (process.env.ENV === 'production') {
    // TODO Produktion
} else {
    // Development
    Error.stackTraceLimit = Infinity

    // tslint:disable:no-require-imports
    // tslint:disable:no-var-requires
    require('zone.js/dist/long-stack-trace-zone')
    // tslint:enable
}
