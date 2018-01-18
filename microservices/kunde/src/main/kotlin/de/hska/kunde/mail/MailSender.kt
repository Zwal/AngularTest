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
package de.hska.kunde.mail

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import de.hska.kunde.config.MailProps
import de.hska.kunde.entity.Kunde
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.stereotype.Service
import reactor.core.publisher.toMono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord

/**
 * Listener fuer Kunden-Ereignisse.
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Service
internal class MailSender (
        private val kafkaSender: KafkaSender<String, String>,
        private val objectMapper: ObjectMapper,
        private val props: MailProps,
        private val fallback: MailSenderFallback) {

    @HystrixCommand(fallbackMethod = "sendFallback")
    fun send(neuerKunde: Kunde) {
        val mailRecord = MailRecord(
                to = props.sales,
                from = props.from,
                subject = "Neuer Kunde",
                body = "<b>Neuer Kunde:</b> <i>${neuerKunde.nachname}</i>")
        // String mit JSON-Datensatz durch Jackson
        val msg = objectMapper.writeValueAsString(mailRecord)
        LOGGER.trace("msg: $msg")
        val kafkaRecord = ProducerRecord(props.topic, "", msg)
        // Verzicht auf korrelierende Metadaten (= null) zur spaeteren Pruefung
        val reactorRecord = SenderRecord.create(kafkaRecord, null)
        // asynchrones Senden
        kafkaSender.send(reactorRecord.toMono()).subscribe()
    }

    @Suppress("unused")
    fun sendFallback(neuerKunde: Kunde) =
        fallback.send(neuerKunde)

    private companion object {
        val LOGGER = getLogger()
    }
}
