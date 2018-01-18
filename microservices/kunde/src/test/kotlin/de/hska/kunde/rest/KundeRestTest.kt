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
package de.hska.kunde.rest

import de.hska.kunde.config.Settings.DEV
import de.hska.kunde.config.security.SimpleUser
import de.hska.kunde.entity.Adresse
import de.hska.kunde.entity.GeschlechtType.WEIBLICH
import de.hska.kunde.entity.InteresseType.LESEN
import de.hska.kunde.entity.InteresseType.REISEN
import de.hska.kunde.entity.InteresseType.SPORT
import de.hska.kunde.entity.Kunde
import de.hska.kunde.entity.Kunde.Companion.ID_PATTERN
import de.hska.kunde.entity.Umsatz
import de.hska.kunde.rest.util.PatchOperation

import java.math.BigDecimal.ONE
import java.net.URL
import java.nio.file.Paths
import java.nio.file.Files.readAllBytes
import java.time.LocalDate
import java.util.Currency
import java.util.Locale.GERMAN
import java.util.Locale.GERMANY

import org.apache.logging.log4j.LogManager.getLogger
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
        .RANDOM_PORT
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpHeaders.IF_MATCH
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.MediaType.IMAGE_PNG
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions
        .basicAuthentication
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.toMono

@Tag("rest")
@ExtendWith(SpringExtension::class)
// Alternative zu @ContextConfiguration von Spring
// Default: webEnvironment = MOCK, d.h.
//          Mock Servlet Umgebung anstatt eines Embedded Servlet Containers
@SpringBootTest(webEnvironment = RANDOM_PORT)
// @SpringBootTest(webEnvironment = DEFINED_PORT, ...)
// ggf.: @DirtiesContext, falls z.B. ein Spring Bean modifiziert wurde
@ActiveProfiles(DEV)
@TestPropertySource(locations = ["/rest-test.properties"])
@DisplayName("Integrationstest fuer den Microservice \"kunde\"")
internal class KundeRestTest (@LocalServerPort private val port: Int) {
    // WebClient auf der Basis von "Reactor Netty"
    private lateinit var client: WebClient
    private lateinit var baseUrl: String

    @BeforeAll
    @Suppress("unused")
    fun beforeAll() {
        val schema = "http"
        baseUrl = "$schema://$HOST:$port"
        LOGGER.info("baseUri = {}", baseUrl)
        client = WebClient.builder()
                .filter(basicAuthentication(USERNAME, PASSWORD))
                .baseUrl(baseUrl)
                .build()
    }

    @Test
    fun `Immer true`() {
        // Given

        // When

        // Then
        assertThat(true).isTrue()
    }

    @Test
    @Disabled("Noch nicht fertig")
    fun `Noch nicht fertig`() {
        // Given

        // When

        // Then
        assertThat(true).isFalse()
    }

    // -------------------------------------------------------------------------
    // L E S E N
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Tests zum Lesen")
    internal inner class ReadTest {
        @Nested
        @DisplayName("Tests zum Lesen anhand der ID")
        internal inner class IdTest {
            @Test
            fun `Suche mit vorhandener ID`() {
                // Given
                val id = ID_VORHANDEN

                // When
                val kunde = client.get()
                        .uri { it.path(ID_PATH).build(id) }
                        .retrieve()
                        .bodyToMono<Kunde>()
                        .block() // alternativ:  subscribe { // <Then> }

                // Then
                LOGGER.debug("Gefundener Kunde = {}", kunde)
                assertSoftly {
                    it.assertThat(kunde).isNotNull
                    it.assertThat(kunde!!.nachname).isNotEmpty
                    it.assertThat(kunde.email).isNotEmpty
                    it.assertThat(kunde._links!!["self"]!!["href"])
                                .endsWith("/$id")
                }
            }

            @Test
            fun `Suche mit syntaktisch ungueltiger ID`() {
                // Given
                val id = ID_INVALID

                // When
                val response = client.get()
                        .uri { it.path(ID_PATH).build(id) }
                        .exchange()
                        .block()!!

                // Then
                assertThat(response.statusCode()).isEqualTo(NOT_FOUND)
            }

            @Test
            fun `Suche mit nicht vorhandener ID`() {
                // Given
                val id = ID_NICHT_VORHANDEN

                // When
                val response = client.get()
                        .uri { it.path(ID_PATH).build(id) }
                        .exchange()
                        .block()!!

                // Then
                assertThat(response.statusCode()).isEqualTo(NOT_FOUND)
            }

            @Test
            fun `Suche mit ID, aber falschem Passwort`() {
                // Given
                val id = ID_VORHANDEN
                val clientFalsch = WebClient.builder()
                        .filter(basicAuthentication(USERNAME, PASSWORD_FALSCH))
                        .baseUrl(baseUrl)
                        .build()

                // When
                val response = clientFalsch.get()
                        .uri { it.path(ID_PATH).build(id) }
                        .exchange()
                        .block()!!

                // Then
                assertThat(response.statusCode()).isEqualTo(UNAUTHORIZED)
            }
        }

        @Test
        fun `Suche nach allen Kunden`() {
            // Given

            // When
            val kunden = client.get()
                    .retrieve()
                    .bodyToFlux<Kunde>()
                    .collectList()
                    .block()

            // Then
            assertThat(kunden).isNotEmpty
        }

        @Test
        fun `Suche mit vorhandenem Nachnamen`() {
            // Given
            val nachname = NACHNAME.toLowerCase(GERMAN)

            // When
            val kunden = client.get()
                    .uri {
                        it.path(KUNDE_PATH)
                                .queryParam(NACHNAME_PARAM, nachname)
                                .build()
                    }
                    .retrieve()
                    .bodyToFlux<Kunde>()
                    .collectList()
                    .block()

            // Then
            assertSoftly { softly ->
                softly.assertThat(kunden).isNotEmpty
                kunden!!.map { it.nachname }
                        .forEach {
                            softly.assertThat(it)
                                    .isEqualToIgnoringCase(nachname)
                        }
            }
        }

        @Test
        fun `Suche mit vorhandener Email`() {
            // Given
            val email = EMAIL_VORHANDEN

            // When
            val kunden = client.get()
                    .uri {
                        it.path(KUNDE_PATH)
                            .queryParam(EMAIL_PARAM, email)
                            .build()
                    }
                    .retrieve()
                    .bodyToFlux<Kunde>()
                    .collectList()
                    .block()

            // Then
            assertSoftly {
                it.assertThat(kunden).isNotEmpty
                val emails = kunden!!.map { it.email }
                it.assertThat(emails).hasSize(1)
                it.assertThat(emails[0]).isEqualToIgnoringCase(email)
            }
        }
    }

    // -------------------------------------------------------------------------
    // S C H R E I B E N
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("Tests zum Schreiben")
    internal inner class WriteTest {
        @Nested
        @DisplayName("Tests zum Neuanlegen")
        internal inner class CreateTest {
            @Test
            fun `Abspeichern eines neuen Kunden`() {
                // Given
                val homepage = URL(NEUE_HOMEPAGE)
                val adresse = Adresse(plz = NEUE_PLZ, ort = NEUER_ORT, strasse = NEUE_STRASSE)
                val user = SimpleUser(
                        id = null,
                        username = NEUER_USERNAME,
                        password = "p",
                        authorities = emptyList())
                val neuerKunde = Kunde(
                        id = null,
                        nachname = NEUER_NACHNAME,
                        email = NEUE_EMAIL,
                        newsletter = true,
                        geburtsdatum = NEUES_GEBURTSDATUM,
                        umsatz = umsatz,
                        homepage = homepage,
                        geschlecht = WEIBLICH,
                        interessen = listOf(LESEN, REISEN),
                        adresse = adresse)
                neuerKunde.user = user

                // When
                val response = client.post()
                        .body(neuerKunde.toMono())
                        .exchange()
                        .block()!!

                // Then
                with (response) {
                    assertSoftly {
                        it.assertThat(statusCode()).isEqualTo(CREATED)
                        it.assertThat(headers()).isNotNull
                        val location = headers().asHttpHeaders().location
                        it.assertThat(location).isNotNull
                        val locationStr = location.toString()
                        it.assertThat(locationStr).isNotEmpty
                        val indexLastSlash = locationStr.lastIndexOf('/')
                        it.assertThat(indexLastSlash).isPositive
                        val idStr = locationStr.substring(indexLastSlash + 1)
                        it.assertThat(idStr).matches(ID_PATTERN)
                    }
                }
            }

            @Test
            fun `Abspeichern eines neuen Kunden mit ungueltigen Werten`() {
                // Given
                val adresse = Adresse(plz = NEUE_PLZ_INVALID, ort = NEUER_ORT, strasse = NEUE_STRASSE)
                val neuerKunde = Kunde(
                        id = null,
                        nachname = NEUER_NACHNAME_INVALID,
                        email = NEUE_EMAIL_INVALID,
                        newsletter = true,
                        geburtsdatum = NEUES_GEBURTSDATUM,
                        geschlecht = WEIBLICH,
                        interessen = listOf(LESEN, REISEN),
                        adresse = adresse)

                // When
                val response = client.post()
                        .body(neuerKunde.toMono())
                        .exchange()
                        .block()!!

                // Then
                with (response) {
                    assertSoftly {
                        assertThat(statusCode()).isEqualTo(BAD_REQUEST)
                        val body = bodyToMono<String>().block()
                        it.assertThat(body).contains("ist nicht 5-stellig")
                        it.assertThat(body).contains("Bei Nachnamen ist nach einem")
                        it.assertThat(body).contains("Die EMail-Adresse")
                    }
                }
            }
        }

        @Nested
        @DisplayName("Tests zum Aktualisieren")
        internal inner class UpdateTest {
            @Test
            fun `Aendern eines vorhandenen Kunden durch Put`() {
                // Given
                val id = ID_UPDATE_PUT

                val responseOrig = client.get()
                        .uri { it.path("$KUNDE_PATH/$id").build() }
                        .exchange()
                        .block()
                val kundeOrig = responseOrig!!
                        .bodyToMono<Kunde>()
                        .block()!!
                assertThat(kundeOrig).isNotNull()
                val kunde = Kunde(
                        id = id,
                        nachname = kundeOrig.nachname,
                        email = "${kundeOrig.email}put",
                        kategorie = kundeOrig.kategorie,
                        newsletter = kundeOrig.newsletter,
                        geburtsdatum = kundeOrig.geburtsdatum,
                        umsatz = kundeOrig.umsatz,
                        homepage = kundeOrig.homepage,
                        geschlecht = kundeOrig.geschlecht,
                        familienstand = kundeOrig.familienstand,
                        interessen = kundeOrig.interessen,
                        adresse = kundeOrig.adresse,
                        username = kundeOrig.username)

                val etag = responseOrig.headers().asHttpHeaders().eTag
                assertThat(etag).isNotNull()
                val version = etag!!.substring(1, etag.length - 1)
                val versionInt = version.toInt() + 1

                // When
                val response = client.put()
                        .uri { it.path(ID_PATH).build(id) }
                        .header(IF_MATCH, versionInt.toString())
                        .body(kunde.toMono())
                        .exchange()
                        .block()!!

                // Then
                with(response) {
                    assertSoftly {
                        it.assertThat(statusCode()).isEqualTo(NO_CONTENT)
                        it.assertThat(bodyToMono<String>()
                                .hasElement()
                                .block()).isFalse
                    }
                }
                // ggf. noch GET-Request, um die Aenderung zu pruefen
            }

            @Test
            fun `Aendern eines vorhandenen Kunden durch Patch`() {
                // Given
                val id = ID_UPDATE_PATCH

                val replaceOp = PatchOperation(
                        op = "replace",
                        path = "/email",
                        value = "${NEUE_EMAIL}patch")
                val addOp = PatchOperation(
                        op = "add",
                        path = "/interessen",
                        value = NEUES_INTERESSE.value)
                val removeOp = PatchOperation(
                        op = "remove",
                        path = "/interessen",
                        value = ZU_LOESCHENDES_INTERESSE.value)
                val operations = listOf(replaceOp, addOp, removeOp)

                val responseOrig = client.get()
                        .uri { it.path("$KUNDE_PATH/$id").build() }
                        .exchange()
                        .block()
                val etag = responseOrig!!.headers().asHttpHeaders().eTag
                assertThat(etag).isNotNull()
                val version = etag!!.substring(1, etag.length - 1)
                val versionInt = version.toInt() + 1

                // When
                val response = client.patch()
                        .uri { it.path(ID_PATH).build(id) }
                        .header(IF_MATCH, versionInt.toString())
                        .body(operations.toMono())
                        .exchange()
                        .block()!!

                // Then
                with(response) {
                    assertSoftly {
                        it.assertThat(statusCode()).isEqualTo(NO_CONTENT)
                        it.assertThat(bodyToMono<String>()
                                .hasElement()
                                .block()).isFalse
                    }
                }
                // ggf. noch GET-Request, um die Aenderung zu pruefen
            }
        }

        @Nested
        @DisplayName("Tests mit multimedialen Daten")
        internal inner class MultimediaTest {
            @Test
            fun `Upload und Download eines PNG-Bildes als Binaerdatei`() {
                // Given
                val id = ID_UPDATE_PNG
                val image = Paths.get("config", "rest", "image.png")
                val bytesUpload = readAllBytes(image)

                // When
                val responseUpload = client.put()
                        .uri { it.path(MULTIMEDIA_PATH).build(id) }
                        .header(CONTENT_TYPE, IMAGE_PNG.toString())
                        .body(bytesUpload.toMono())
                        .exchange()
                        .block()!!

                // Then
                assertThat(responseUpload.statusCode()).isEqualTo(NO_CONTENT)

                val responseDownload = client.get()
                        .uri { it.path(MULTIMEDIA_PATH).build(id) }
                        .accept(IMAGE_PNG)
                        .exchange()
                        .block()

                assertSoftly {
                    it.assertThat(responseDownload).isNotNull
                    it.assertThat(responseDownload!!.statusCode())
                            .isEqualTo(OK)
                    // ggf. responseDownload.body(toDataBuffers())
                }
            }
        }

        @Nested
        @DisplayName("Tests zum Loeschen")
        internal inner class DeleteTest {
            @Test
            fun `Loeschen eines vorhandenen Kunden mit der ID`() {
                // Given
                val id = ID_DELETE

                // When
                val response = client.delete()
                        .uri { it.path(ID_PATH).build(id) }
                        .exchange()
                        .block()!!

                // Then
                assertThat(response.statusCode()).isEqualTo(NO_CONTENT)
            }

            @Test
            fun `Loeschen eines vorhandenen Kunden mit Emailadresse`() {
                // Given
                val email = EMAIL_DELETE

                // When
                val response = client.delete()
                    .uri {
                        it.path(KUNDE_PATH)
                            .queryParam(EMAIL_PARAM, email)
                            .build()
                    }
                    .exchange()
                    .block()!!

                // Then
                assertThat(response.statusCode()).isEqualTo(NO_CONTENT)
            }
        }
    }

    private companion object {
        val HOST = "localhost"
        val KUNDE_PATH = "/"
        val ID_PATH = "/{id}"
        val NACHNAME_PARAM = "nachname"
        val EMAIL_PARAM = "email"
        val MULTIMEDIA_PATH = "/multimedia/{id}"
        val USERNAME = "admin"
        val PASSWORD = "p"
        val PASSWORD_FALSCH = "?!$"

        val ID_VORHANDEN = "00000000-0000-0000-0000-000000000001"
        val ID_INVALID = "YYYYYYYY-YYYY-YYYY-YYYY-YYYYYYYYYYYY"
        val ID_NICHT_VORHANDEN = "99999999-9999-9999-9999-999999999999"
        val ID_UPDATE_PUT = "00000000-0000-0000-0000-000000000002"
        val ID_UPDATE_PATCH = "00000000-0000-0000-0000-000000000003"
        val ID_UPDATE_PNG = "00000000-0000-0000-0000-000000000003"
        val ID_DELETE = "00000000-0000-0000-0000-000000000004"
        val EMAIL_VORHANDEN = "alpha@hska.edu"
        val EMAIL_DELETE = "phi@hska.cn"

        val NACHNAME = "alpha"

        val NEUE_PLZ = "12345"
        val NEUE_PLZ_INVALID = "1234"
        val NEUER_ORT = "Testort"
        val NEUE_STRASSE = "Teststr. 2"
        val NEUER_NACHNAME = "Neuernachname"
        val NEUER_NACHNAME_INVALID = "?!$"
        val NEUE_EMAIL = "email@test.de"
        val NEUE_EMAIL_INVALID = "email@"
        val NEUES_GEBURTSDATUM = LocalDate.of(2016, 1, 31)
        val NEUE_WAEHRUNG = Currency.getInstance(GERMANY)
        val NEUE_HOMEPAGE = "https://test.de"
        val NEUER_USERNAME = "test"

        val NEUES_INTERESSE = SPORT
        val ZU_LOESCHENDES_INTERESSE = LESEN

        val LOGGER = getLogger()
    }
}
