/*
 * Copyright (C) 2015 - 2016 Juergen Zimmermann, Hochschule Karlsruhe
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

import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core'
import {Title} from '@angular/platform-browser'

import {log} from '../../shared'
import {BuchService} from '../shared/buch.service'

/**
 * Komponente mit dem Tag &lt;hs-balkendiagramm&gt; zur Visualisierung
 * von Bewertungen durch ein Balkendiagramm.
 */
@Component({
    selector: 'hs-balkendiagramm',
    template: `
        <section id="diagramm">
            <canvas #chartCanvas width="300" height="150"></canvas>
        </section>
    `,
})
export default class BalkendiagrammComponent implements AfterViewInit {
    @ViewChild('chartCanvas') chartCanvas: ElementRef

    constructor(
        private readonly buchService: BuchService,
        private readonly titleService: Title) {
        console.log('BalkendiagrammComponent.constructor()')
    }

    /**
     * Das Balkendiagramm beim Tag <code><canvas></code> einf&uuml;gen.
     * Erst in ngAfterViewInit kann auf ein Kind-Element aus dem Templates
     * zugegriffen werden:
     * https://angular.io/docs/ts/latest/guide/lifecycle-hooks.html.
     */
    @log
    ngAfterViewInit() {
        const chartElement: HTMLCanvasElement = this.chartCanvas.nativeElement
        this.buchService.createBarChart(chartElement)

        this.titleService.setTitle('Balkendiagramm')
    }

    toString() {
        return 'BalkendiagrammComponent'
    }
}
