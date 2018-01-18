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
package de.hska.kafkamailer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.Duration
import java.util.Properties
import org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.CLIENT_ID_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig
        .KEY_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig
        .VALUE_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMailMessage
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions

object KafkaMailer {
    private val topic = "mail"

    private val bootstrapHost = "localhost"
    // Default-Port von Kafka
    private val bootstrapPort = 9092
    private val bootstrapServers = "$bootstrapHost:$bootstrapPort"
    private val props = mapOf(
        BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
        CLIENT_ID_CONFIG to "mail-consumer",
        GROUP_ID_CONFIG to "mail-group",
        KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        // ggf. eigener Deserializer fuer Binaer- bzw. multimediale Dateien
        VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        AUTO_OFFSET_RESET_CONFIG to "earliest")

    private val receiverOptions = ReceiverOptions
            .create<String, String>(props)
            .subscription(listOf(topic))
            .addAssignListener { LOGGER.info("Zugewiesene Partition: $it") }
            .addRevokeListener { LOGGER.info("Entfernte Partition: $it") }
            .commitInterval(Duration.ZERO)

    private val kafkaReceiver =
            KafkaReceiver.create<String, String>(receiverOptions)

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    private val MAILHOST = "localhost"
    private val MAILPORT = 25000
    private val javaMailSender = JavaMailSenderImpl().apply {
        host = MAILHOST
        port = MAILPORT

        // https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/...
        // ...package-summary.html
        val falseStr = false.toString()
        val properties = Properties().apply {
            setProperty("mail.transport.protocol", "smtp")
            setProperty("mail.smtp.auth", falseStr)
            setProperty("mail.smtp.starttls.enable", falseStr)
            setProperty("mail.debug", falseStr)
        }
        javaMailProperties = properties
    }

    private val LOGGER = getLogger()

    fun flux() =
        kafkaReceiver.receive()
            .publishOn(Schedulers.newSingle("mailScheduler", true))
            .concatMap { record ->
                sendMail(record.value())
                    .doOnSuccess {
                        // Kafka-Record aus Topic entfernen
                        record.receiverOffset().commit().block()
                    }
            }
            .retry()

    private fun sendMail(mailStr: String): Mono<Void> {
        val mailRecord = objectMapper.readValue(mailStr, MailRecord::class.java)
        LOGGER.debug(mailRecord)

        val mimeMessage = javaMailSender.createMimeMessage()
        val msg = MimeMailMessage(mimeMessage)
        with (msg) {
            setFrom(mailRecord.from)
            setTo(mailRecord.to)
            setSubject(mailRecord.subject)
            setText(mailRecord.body)
        }

        javaMailSender.send(msg.mimeMessage)
        return Mono.empty()
    }
}

fun main(args: Array<String>) {
    KafkaMailer.flux().blockLast()
}
