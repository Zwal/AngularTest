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
                vorname ="Adam",
                email = "admin@hska.de",
                kategorie = 0,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 31),
                homepage = URL("https://www.hska.de"),
                geschlecht = GeschlechtType.build("W"),
                familienstand = FamilienstandType.build("VH"),
                interessen = listOf(InteresseType.build("L")!!),
                adresse = Adresse("00000", "Aachen", "Bachweg 2"),  //PLZ, Stadt, Straße u. Hausnummer
                username = "admin"
            ),
            Kunde(
                id ="00000000-0000-0000-0000-000000000001",
                nachname = "Alpha",
                vorname ="Alph",
                email = "alpha@hska.edu",
                kategorie = 1,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 1),
                homepage = URL("https://www.hska.edu"),
                geschlecht = GeschlechtType.build("M"),
                familienstand = FamilienstandType.build("L"),
                interessen = listOf(
                    InteresseType.build("S")!!,
                    InteresseType.build("L")!!),
                adresse = Adresse("11111", "Augsburg", "Hefestr. 46"),
                username = "alpha1"
            ),
            Kunde(
                id ="00000000-0000-0000-0000-000000000002",
                nachname = "Alpha",
                vorname ="Alphine",
                email = "alpha@hska.ch",
                kategorie = 2,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 2),
                homepage = URL("https://www.hska.ch"),
                geschlecht = GeschlechtType.build("W"),
                familienstand = FamilienstandType.build("G"),
                interessen = listOf(
                    InteresseType.build("S")!!,
                    InteresseType.build("R")!!),
                adresse = Adresse("22222", "Aalen", "Moltkestr. 12"),
                username = "alpha2"
            ),
            Kunde(
                id ="00000000-0000-0000-0000-000000000003",
                nachname = "Alpha",
                vorname ="Alpha",
                email = "alpha@hska.uk",
                kategorie = 3,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 3),
                homepage = URL("https://www.hska.uk"),
                geschlecht = GeschlechtType.build("M"),
                familienstand = FamilienstandType.build("VW"),
                interessen = listOf(
                    InteresseType.build("L")!!,
                    InteresseType.build("R")!!),
                adresse = Adresse("33333", "Ahlen", "Ahlstr. 2"),
                username = "alpha3"
            ),
            Kunde(
                id ="00000000-0000-0000-0000-000000000004",
                nachname = "Delta",
                vorname ="Delle",
                email = "delta@hska.jp",
                kategorie = 4,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 4),
                homepage = URL("https://www.hska.jp"),
                geschlecht = GeschlechtType.build("W"),
                familienstand = FamilienstandType.build("VH"),
                interessen = null,
                adresse = Adresse("44444", "Dortmund", "Blastr. 2"),
                username = "delta"
            ),
            Kunde(
                id ="00000000-0000-0000-0000-000000000005",
                nachname = "Epsilon",
                vorname ="Emil",
                email = "epsilon@hska.cn",
                kategorie = 5,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 5),
                homepage = URL("https://www.hska.cn"),
                geschlecht = GeschlechtType.build("M"),
                familienstand = FamilienstandType.build("L"),
                interessen = null,
                adresse = Adresse("55555", "Essen", "Blisztr 4"),
                username = "epsilon"
            ),
            Kunde(
                id ="00000000-0000-0000-0000-000000000006",
                nachname = "Klose",
                vorname ="Miroslav",
                email = "phi@hska.cn",
                kategorie = 6,
                newsletter = true,
                geburtsdatum = LocalDate.of(2017, 1, 6),
                homepage = URL("https://www.hska.cn"),
                geschlecht = GeschlechtType.build("M"),
                familienstand = FamilienstandType.build("L"),
                interessen = null,
                adresse = Adresse("66666", "Freiburg", "Freiburgerstr. 12"),
                username = "phi"
            )
        )

        val LOGGER = getLogger(DbReload::class.java)!!
    }
}
