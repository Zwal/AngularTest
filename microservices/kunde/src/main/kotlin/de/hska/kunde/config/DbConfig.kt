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
package de.hska.kunde.config

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoClientURI
import de.hska.kunde.config.DbReactiveConfig.Companion.APP_NAME
import de.hska.kunde.config.DbReactiveConfig.Companion.SSL_CONTEXT
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import javax.annotation.PreDestroy

/**
 * Spring-Konfiguration fuer den synchronen Zugriff auf MongoDB mit TLS;
 * erforderlich fuer GridFS.
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Configuration
internal class DbConfig (val props: DbProps) : AbstractMongoConfiguration() {
    private var mongoClient: MongoClient? = null

    override fun getDatabaseName() = props.dbname

    @Bean
    @Suppress("UselessPostfixExpression")
    override fun mongoClient(): MongoClient {
        var uri = "mongodb://"
        with(props) {
            uri += "$username:$password@$dbhost/$dbname?authSource=$authDb"
        }
        LOGGER.info("DB-URI: $uri")

        val optionsBuilder = MongoClientOptions.builder()
                .applicationName(APP_NAME)
                .description(APP_NAME)
                .apply {
                    with(props) {
                        // http://docs.mlab.com/connecting
                        // Connections per Host: 100
                        // Max Connection Idle Time: 60000
                        connectionsPerHost(maxConnectionsPerHost)
                        if (tls) {
                            sslEnabled(true)
                                    .socketFactory(SSL_CONTEXT.socketFactory)
                        }
                    }
                }

        val mongoClientUri = MongoClientURI(uri, optionsBuilder)
        mongoClient = MongoClient(mongoClientUri)
        return mongoClient!!
    }

    @PreDestroy
    fun close() {
        LOGGER.info("MongoClient wird geschlossen")
        mongoClient?.close()
    }

    override fun customConversions() =
        MongoCustomConversions(DbReactiveConfig.CONVERTERS)

    private companion object {
        val LOGGER = getLogger()
    }
}
