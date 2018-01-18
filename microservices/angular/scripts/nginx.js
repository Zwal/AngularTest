/*
 * Copyright (C) 2016 Juergen Zimmermann, Hochschule Karlsruhe
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

/* global require, process, __dirname, console */
/* eslint no-magic-numbers: 0*/
/* eslint no-console: 0 */

const minimist = require('minimist')
const shell = require('shelljs')
const fs = require('fs')
const path = require('path')
const slash = require('slash')

const argv = minimist(process.argv.slice(0))
const values = argv._
const start = values[2] === undefined || values[2] === 'start'
const stop = values[2] === 'stop'
const init = values[2] === 'init'

if (start) {
    console.log('nginx startet ohne Meldung...')
    shell.exec('cd C:/Zimmermann/nginx && nginx')
} else if (stop) {
    shell.exec('cd C:/Zimmermann/nginx && nginx -s quit')
} else if (init) {
    const constants = require('./constants')

    // Bei file:/// erlaubt ein Browser aus Sicherheitsgründen keine Ajax-Requests,
    // weil JavaScript sonst direkt im Dateisystem lesen koennte
    const {dist, nginx} = constants.dir
    const basedir = slash(path.join(__dirname, '..', dist))
    const newConfFile = path.join(nginx, 'conf', 'nginx.conf')

    const readCb = (err, data) => {
        if (err) {
            return console.log(err)
        }
        const result = data.replace(/BASEDIR_JZ/g, basedir)

        const errCb = errWrite => {
            if (errWrite) {
                return console.log(errWrite)
            }
        }
        fs.writeFile(newConfFile, result, 'utf8', errCb)
    }

    fs.readFile('config/webserver/nginx/nginx.conf', 'utf8', readCb)
}
