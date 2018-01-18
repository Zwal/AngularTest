/*
 * Copyright (C) 2017 Juergen Zimmermann, Hochschule Karlsruhe
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

/* global require, __dirname, console */
/* eslint no-console: 0, no-magic-numbers: 0 */

const path = require('path')
const shell = require('shelljs')

const nightwatch = path.join(
    __dirname,
    '..',
    'node_modules',
    'nightwatch',
    'bin',
    'nightwatch')

const verbose = ''
// const verbose = '--verbose'
const tag = ''
// const tag = '--tag login'

shell.exec(`node ${nightwatch} ${verbose} ${tag}`)
