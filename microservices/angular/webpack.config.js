/*
 * Copyright (C) 2017 Juergen Zimmermann, Fabian Hinz, Gabriel Pollak
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

/* global require, module, __dirname */

const path = require('path')
// http://github.com/webpack/webpack
const webpack = require('webpack')
// http://github.com/jantimon/html-webpack-plugin
const HtmlWebpackPlugin = require('html-webpack-plugin')
// http://github.com/webpack-contrib/extract-text-webpack-plugin
const ExtractTextPlugin = require('extract-text-webpack-plugin')
// http://github.com/kevlened/copy-webpack-plugin
const CopyWebpackPlugin = require('copy-webpack-plugin')
// https://github.com/lodash/lodash-webpack-plugin
const LodashModuleReplacementPlugin = require('lodash-webpack-plugin')
// https://github.com/th0r/webpack-bundle-analyzer
const {BundleAnalyzerPlugin} = require('webpack-bundle-analyzer')

// https://github.com/webpack/docs/wiki/configuration
// https://angular.io/docs/ts/latest/guide/webpack.html
// https://toddmotto.com/lazy-loading-angular-code-splitting-webpack
module.exports = {
    entry: {
        polyfills: './src/polyfills.ts',
        angular: './src/angular.ts',
        others: './src/others.ts',
        shared: './src/shared/index.ts',
        app: './src/app/index.ts',
    },

    output: {
        path: path.resolve(__dirname, 'dist'),
        // polyfills.js, angular.js, others.js, shared.js, app.js
        filename: '[name].js',
    },

    module: {
        // Regeln fuer das Laden von Dateitypen
        rules: [
            {
                // Source-Maps fuer uebersetzte JavaScript-Dateien
                enforce: 'pre',
                test: /\.(js|css)$/,
                // http://github.com/webpack-contrib/source-map-loader
                use: ['source-map-loader'],
            },
            // TODO TSLint
            {
                enforce: 'pre',
                test: /\.ts$/,
                // https://github.com/wbuchwalter/tslint-loader
                loader: 'tslint-loader',
                options: {
                    configFile: 'tslint.json',
                    failOnHint: true,
                    typeCheck: true,
                },
                exclude: [/node_modules/],
            },
            {
                test: /\.ts$/,
                // https://github.com/s-panferov/awesome-typescript-loader
                // https://github.com/TypeStrong/ts-loader
                // http://github.com/TheLarkInn/angular2-template-loader
                loaders: [
                    'awesome-typescript-loader',
                    'angular2-template-loader',
                ],
            },
            {
                // HTML-Fragmente fuer Komponenten in Angular
                test: /\.html$/,
                // http://github.com/webpack-contrib/html-loader
                loaders: 'html-loader',
            },
            {
                // Sass-Dateien für styleUrls in Komponenten in Angular
                test: /\.scss$/,
                // http://github.com/gajus/to-string-loader
                // http://github.com/webpack-contrib/css-loader
                // http://github.com/webpack-contrib/sass-loader
                loaders: ['to-string-loader', 'css-loader', 'sass-loader'],
            },
            {
                // CSS-Dateien für styleUrls in Komponenten in Angular
                test: /\.css$/,
                loader: ExtractTextPlugin.extract({
                    // http://github.com/webpack-contrib/style-loader
                    fallback: 'style-loader',
                    use: 'css-loader',
                }),
            },
            {
                test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)$/,
                // http://github.com/webpack-contrib/file-loader
                loader: 'file-loader?name=assets/[name].[hash].[ext]',
            },
            // {
            //     loader: 'babel-loader',
            //     test: /\.js$/,
            //     exclude: /node_modules/,
            //     query: {
            //         plugins: [
            //             'lodash', {
            //                 id: ['version', 'times'],
            //             },
            //         ],
            //         presets: [
            //             [
            //                 'env', {
            //                     targets: {
            //                         node: 4,
            //                     },
            //                 },
            //             ],
            //         ],
            //     },
            // },
        ],
    },

    // Zulaessige Endungen bei "import"
    resolve: {
        extensions: ['.ts', '.js', '.scss', '.css', '.html'],
    },

    plugins: [
        // Abbruch im Fehlerfall
        new webpack.NoEmitOnErrorsPlugin(),
        new webpack.optimize.OccurrenceOrderPlugin(),

        // Optimierung bzw. Minification, falls nach ES5 (siehe tsconfig.json) uebersetzt wird
        // sonst: BabiliPlugin
        // https://github.com/AngularClass/angular2-webpack-starter/blob/master/config/webpack.prod.js
        new webpack.optimize.UglifyJsPlugin(),
        // new webpack.optimize.UglifyJsPlugin({
        //     beautify: false,
        //     output: {
        //         comments: false,
        //     },
        //     mangle: {
        //         screw_ie8: true,
        //     },
        //     compress: {
        //         screw_ie8: true,
        //         warnings: false,
        //         conditionals: true,
        //         unused: true,
        //         comparisons: true,
        //         sequences: true,
        //         dead_code: true,
        //         evaluate: true,
        //         if_return: true,
        //         join_vars: true,
        //         negate_iife: false,
        //     },
        // }),

        // Anwendung gemaess der "entry"-Points (s.o.) in Chunks aufsplitten
        new webpack.optimize.CommonsChunkPlugin({
            name: ['app', 'shared', 'others', 'angular', 'polyfills'],
        }),

        new webpack.LoaderOptionsPlugin({
            options: {
                tslintLoader: {
                    emitErrors: true,
                    failOnHint: true,
                },
            },
            // htmlLoader: {
            //     minimize: false
            // }
        }),

        // Module nachladen: jQuery und Tether fuer Bootstrap
        // https://getbootstrap.com/docs/4.0/getting-started/webpack
        new webpack.ProvidePlugin({
            $: 'jquery',
            jQuery: 'jquery',
            Popper: ['popper.js', 'default'],
            'window.jQuery': 'jquery',
        }),

        // http://github.com/webpack-contrib/extract-text-webpack-plugin
        // aus .css und .scss-Dateien wird app.css erstellt: siehe index.ts
        new ExtractTextPlugin({
            filename: 'app.css',
            allChunks: true,
        }),

        // http://github.com/jantimon/html-webpack-plugin
        // index.html erstellen einschl. Chunks fuer JS und app.css (s.o.)
        new HtmlWebpackPlugin({
            template: 'src/index.html',
        }),

        // Bilder usw. kopieren, die nicht in einem Chunk enthalten sind
        // http://github.com/kevlened/copy-webpack-plugin
        new CopyWebpackPlugin([
            {
                from: 'src/img',
                to: './img',
            },
            {
                from: 'src/favicon.ico',
                to: './',
            },
        ]),

        // https://webpack.js.org/plugins/context-replacement-plugin
        // http://stackoverflow.com/questions/25384360/how-to-prevent-moment-js-from-loading-locales-with-webpack/25426019#25426019
        new webpack.ContextReplacementPlugin(
            /moment[\/\\]locale/, /de\.js/
        ),

        new LodashModuleReplacementPlugin({
            version: true,
            times: true,
        }),

        // !!! Zur initialen Visualisierung. Nervig beim Entwickeln !!!
        // https://github.com/th0r/webpack-bundle-analyzer
        new BundleAnalyzerPlugin({
            analyzerMode: 'static',
        }),
    ],

    // https://webpack.js.org/configuration/watch
    watch: true,
    // watchOptions: {
    //     // Millisekunden
    //     aggregateTimeout: 300
    // }
}
