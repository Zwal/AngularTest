/*
 * Copyright (C) 2016 - 2017 Juergen Zimmermann, Hochschule Karlsruhe
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

/* global require, process */
/* eslint no-magic-numbers: 0 */

// Alternative: webpack-dev-server
// https://webpack.github.io/docs/webpack-dev-server.html
// https://webpack.js.org/configuration/dev-server

const minimist = require('minimist')
const log = require('connect-logger')
const connectHistoryApiFallback = require('connect-history-api-fallback')
const browserSync = require('browser-sync')

const {dir, port, host, browser} = require('./constants')
const {webserverConfig, dist} = dir

const key = `${webserverConfig}/https/key.pem`
const cert = `${webserverConfig}/https/certificate.cer`
const format = '%date %status %method %url'
const index = '/index.html'
const htmlAcceptHeaders = ['text/html', 'application/xhtml+xml']
const argv = minimist(process.argv.slice(2))
const online = argv.online !== undefined

// Nicht bs-config.js wg. Middleware log() und connectHistoryApiFallback()
const options = {
    server: {
        baseDir: dist,
    },
    https: {
        key,
        cert,
    },
    // httpModule: 'http2',
    port,
    host,
    middleware: [
        log({
            format,
        }),
        connectHistoryApiFallback({
            index,
            htmlAcceptHeaders,
        }),
    ],
    cors: true,
    logConnections: false,
    online,
    ui: false,
    browser,
    reloadOnRestart: true,
    notify: false,
}
browserSync.create().init(options)
