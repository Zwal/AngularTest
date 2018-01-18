@file:Suppress("StringLiteralDuplication")
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
package de.hska.kunde.config.dev

import de.hska.kunde.config.Settings.DEV
import de.hska.kunde.db.KundeRepository
import de.hska.kunde.entity.Adresse
import de.hska.kunde.entity.FamilienstandType
import de.hska.kunde.entity.GeschlechtType
import de.hska.kunde.entity.InteresseType
import de.hska.kunde.entity.Kunde
import de.hska.kunde.entity.Umsatz
import java.math.BigDecimal
import java.net.URL
import java.time.LocalDate
import java.util.Currency
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Description
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.dropCollection
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsTemplate

internal interface DbReload {
    /**
     * Spring Bean, um einen CommandLineRunner fuer das Profil "dev"
     * bereitzustellen.
     * @param gridFsTemplate Template fuer Files
     * @return CommandLineRunner
     */
    @Bean
    @Description("Test-DB neu laden")
    @Profile(DEV)
    fun dbReload(gridFsTemplate: GridFsTemplate,
                 mongoTemplate: ReactiveMongoTemplate,
                 repo: KundeRepository) = CommandLineRunner {
        // alle hochgeladenen multimedialen Dateien loeschen
        LOGGER.warn("Alle multimedialen Dateien werden geloescht")
        gridFsTemplate.delete(Query())
        mongoTemplate.dropCollection<Kunde>()
            .thenMany(mongoTemplate.insertAll(KUNDEN))
            .subscribe { LOGGER.warn(it) }
    }

    private companion object {
        val KUNDEN = listOf(
            Kunde(
                id ="00000000-0000-0000-0000-000000000000",
                nachname = "Admin",
                email = "admin@hska.de",
                kategorie = 0,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 31),
                umsatz = Umsatz(BigDecimal("0"), Currency.getInstance("EUR")),
                homepage = URL("https://www.hska.de"),
                geschlecht = GeschlechtType.build("W"),
                familienstand = FamilienstandType.build("VH"),
                interessen = listOf(InteresseType.build("L")!!),
                adresse = Adresse("00000", "Aachen"),
                username = "admin"
            ),
            Kunde(
                id ="00000000-0000-0000-0000-000000000001",
                nachname = "Alpha",
                email = "alpha@hska.edu",
                kategorie = 1,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 1),
                umsatz = Umsatz(BigDecimal("10"), Currency.getInstance("USD")),
                homepage = URL("https://www.hska.edu"),
                geschlecht = GeschlechtType.build("M"),
                familienstand = FamilienstandType.build("L"),
                interessen = listOf(
                    InteresseType.build("S")!!,
                    InteresseType.build("L")!!),
                adresse = Adresse("11111", "Augsburg"),
                username = "alpha1"
            ),
            Kunde(
                id ="00000000-0000-0000-0000-000000000002",
                nachname = "Alpha",
                email = "alpha@hska.ch",
                kategorie = 2,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 2),
                umsatz = Umsatz(BigDecimal("20"), Currency.getInstance("CHF")),
                homepage = URL("https://www.hska.ch"),
                geschlecht = GeschlechtType.build("W"),
                familienstand = FamilienstandType.build("G"),
                interessen = listOf(
                    InteresseType.build("S")!!,
                    InteresseType.build("R")!!),
                adresse = Adresse("22222", "Aalen"),
                username = "alpha2"
            ),
            Kunde(
                id ="00000000-0000-0000-0000-000000000003",
                nachname = "Alpha",
                email = "alpha@hska.uk",
                kategorie = 3,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 3),
                umsatz = Umsatz(BigDecimal("30"), Currency.getInstance("GBP")),
                homepage = URL("https://www.hska.uk"),
                geschlecht = GeschlechtType.build("M"),
                familienstand = FamilienstandType.build("VW"),
                interessen = listOf(
                    InteresseType.build("L")!!,
                    InteresseType.build("R")!!),
                adresse = Adresse("33333", "Ahlen"),
                username = "alpha3"
            ),
            Kunde(
                id ="00000000-0000-0000-0000-000000000004",
                nachname = "Delta",
                email = "delta@hska.jp",
                kategorie = 4,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 4),
                umsatz = Umsatz(BigDecimal("40"), Currency.getInstance("JPY")),
                homepage = URL("https://www.hska.jp"),
                geschlecht = GeschlechtType.build("W"),
                familienstand = FamilienstandType.build("VH"),
                interessen = null,
                adresse = Adresse("44444", "Dortmund"),
                username = "delta"
            ),
            Kunde(
                id ="00000000-0000-0000-0000-000000000005",
                nachname = "Epsilon",
                email = "epsilon@hska.cn",
                kategorie = 5,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 5),
                umsatz = null,
                homepage = URL("https://www.hska.cn"),
                geschlecht = GeschlechtType.build("M"),
                familienstand = FamilienstandType.build("L"),
                interessen = null,
                adresse = Adresse("55555", "Essen"),
                username = "epsilon"
            ),
            Kunde(
                id ="00000000-0000-0000-0000-000000000006",
                nachname = "Phi",
                email = "phi@hska.cn",
                kategorie = 6,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 6),
                umsatz = null,
                homepage = URL("https://www.hska.cn"),
                geschlecht = GeschlechtType.build("M"),
                familienstand = FamilienstandType.build("L"),
                interessen = null,
                adresse = Adresse("66666", "Freiburg"),
                username = "phi"
            )
        )

        val LOGGER = getLogger(DbReload::class.java)!!
    }
}
