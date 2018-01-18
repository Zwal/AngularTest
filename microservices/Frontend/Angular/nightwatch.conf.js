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

/* global require, module */
/* eslint camelcase: 0 */

// https://www.syncano.io/blog/testing-syncano/#addingecmascript6tonightwatch
require('babel-core/register')

// Konfiguration wie in nightwatch.json: nightwatch.conf.js hat hoehere Prioritaet
const config = {
    src_folders: 'test',
    output_folder: 'reports',
    page_objects_path: [
        'test/auth/pages',
        'test/kunde/pages',
        'test/shared/pages',
    ],
    globals_path: 'nightwatch.js',
    selenium: {
        start_process: false,
    },
    // test_runner : {
    //     type : 'mocha',
    //     options : {
    //         ui : 'bdd',
    //         reporter : 'spec',
    //         // reporter : 'list',
    //     },
    // },
    test_settings: {
        default: {
            launch_url: 'https://localhost',
            selenium_port: 9515,
            selenium_host: '127.0.0.1',
            default_path_prefix: '',
            desiredCapabilities: {
                browserName: 'chrome',
                elementScrollBehavior: '1',
                chromeOptions: {
                    args: [
                        '--no-sandbox',
                        'window-size=1600,1200',
                        '--allow-running-insecure-content',
                        '--disable-security'
                    ],
                },
                acceptSslCerts: true,
            },
        },
    },
    test_workers: false,
}
module.exports = config
