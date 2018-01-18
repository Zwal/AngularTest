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
package de.hska.kunde.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Description
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig
        .KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig
        .VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.common.serialization.StringSerializer
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions

internal interface KafkaConfig {
    @Bean
    @Description("Sender/Producer mittels Reactor Kafka")
    fun kafkaSender() = KafkaSender.create(senderOptions)!!

    private companion object {
        val senderOptions by lazy {
            val bootstrapHost = "localhost"
            // Default-Port von Kafka
            val bootstrapPort = 9092
            val bootstrapServers = "$bootstrapHost:$bootstrapPort"
            val props = mapOf(
                BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
                CLIENT_ID_CONFIG to "mail-producer",
                ACKS_CONFIG to "all",
                KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                // ggf. eigener Serializer fuer z.B. Binaerdateien
                VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java
            )

            // Key vom Typ String, Value vom Typ String
            SenderOptions.create<String, String>(props)
        }
    }
}
