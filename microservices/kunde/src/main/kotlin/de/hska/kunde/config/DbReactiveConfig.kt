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

import com.mongodb.async.client.MongoClientSettings
import com.mongodb.connection.ConnectionPoolSettings
import com.mongodb.connection.SslSettings
import com.mongodb.MongoCredential.createCredential
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import de.hska.kunde.config.security.SimpleUser
import de.hska.kunde.entity.FamilienstandType
import de.hska.kunde.entity.GeschlechtType
import de.hska.kunde.entity.InteresseType
import de.hska.kunde.entity.Kunde
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader.CLASSPATH_URL_PREFIX
import org.springframework.data.mongodb.config
        .AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.util.ResourceUtils.getURL
import com.mongodb.ServerAddress
import com.mongodb.connection.ClusterSettings
import com.mongodb.connection.netty.NettyStreamFactoryFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import javax.annotation.PreDestroy

/**
 * Spring-Konfiguration fuer den asynchronen Zugriff auf MongoDB mit TLS
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Configuration
@EnableMongoAuditing
// DB-Konfiguration: siehe folgende Klassen
//  MongoAutoConfiguration
//  MongoReactiveAutoConfiguration
//  MongoDataAutoConfiguration
//  MongoReactiveDataAutoConfiguration
// http://www.baeldung.com/spring-boot-custom-auto-configuration
@Suppress("LeakingThis")
internal class DbReactiveConfig (val props: DbProps
) : AbstractReactiveMongoConfiguration() {

    private var mongoClient: MongoClient? = null

    private val mongoClientSettings by lazy {
        LOGGER.info("Datenbank: ${props.dbname}")

        // https://docs.mongodb.com/manual/core/authentication/
        // http://mongodb.github.io/mongo-java-driver/3.3/driver-async/...
        //        ...reference/connecting/authenticating
        // default: SCRAM-SHA-1
        val credentials = createCredential(props.username, props.authDb,
                    props.password!!.toCharArray())

        // http://mongodb.github.io/mongo-java-driver/3.3/driver-async/...
        //        ...reference/connecting/connection-settings
        val connectionPoolSettings = ConnectionPoolSettings.builder()
                .maxSize(props.maxConnectionsPerHost)
                .build()
        val clusterSettings = ClusterSettings.builder()
                .hosts(listOf(ServerAddress())).build()

        val mongoClientSettingsBuilder = MongoClientSettings.builder()
                .credentialList(listOf(credentials))
                .connectionPoolSettings(connectionPoolSettings)
                .clusterSettings(clusterSettings)
                .applicationName(APP_NAME)

        if (props.tls) {
            val sslSettings = SslSettings.builder()
                    .enabled(true)
                    .invalidHostNameAllowed(false)
                    .context(SSL_CONTEXT)
                    .build()
            val nettyStreamFactoryFactory =
                    NettyStreamFactoryFactory.builder().build()
            mongoClientSettingsBuilder.sslSettings(sslSettings)
                    .streamFactoryFactory(nettyStreamFactoryFactory)
        }

        mongoClientSettingsBuilder.build()
    }

    override fun getDatabaseName() = props.dbname

    @Suppress("UselessPostfixExpression")
    override fun reactiveMongoClient(): MongoClient {
        mongoClient = MongoClients.create(mongoClientSettings)
        return mongoClient!!
    }

    // http://www.concretepage.com/spring/spring-bean-life-cycle-tutorial
    @PreDestroy
    fun close() = {
        LOGGER.info("Reactive MongoClient wird geschlossen")
        mongoClient?.close()
    }

    override fun getMappingBasePackages() = ENTITY_PACKAGES

    override fun customConversions() = MongoCustomConversions(CONVERTERS)

    companion object {
        private val ENTITY_PACKAGES = listOf(Kunde::class, SimpleUser::class)
                .map { it.java.`package`.name }
        val CONVERTERS = listOf(
                // Enums
                GeschlechtType.ReadConverter(),
                GeschlechtType.WriteConverter(),
                FamilienstandType.ReadConverter(),
                FamilienstandType.WriteConverter(),
                InteresseType.ReadConverter(),
                InteresseType.WriteConverter(),

                // Rollen fuer Security
                SimpleUser.RoleReadConverter(),
                SimpleUser.RoleWriteConverter())

        val APP_NAME: String
            get() {
                val packageName = DbReactiveConfig::class.java.`package`.name
                val parentPackageName =
                        packageName.substring(0, packageName.lastIndexOf('.'))
                return parentPackageName.substring(
                                parentPackageName.lastIndexOf('.') + 1)
            }
        private val MONGODB_CERT_FILENAME = "mongo.cer"
        private val LOGGER = getLogger()

        // https://docs.mongodb.com/manual/reference/connection-string
        // http://blog.carl.pro/2016/07/connecting-to-mongodb-using-ssl-but-...
        // ...without-locally-installed-server-ssl-certificate
        // http://www.programcreek.com/java-api-examples/index.php...
        // ...?api=com.mongodb.MongoClientOptions
        // selbst-signiertes Zertifikat
        val SSL_CONTEXT = SSLContext.getInstance("TLS").apply {
            val trustManager = object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    var cert: X509Certificate? = null
                    getURL("$CLASSPATH_URL_PREFIX$MONGODB_CERT_FILENAME")
                            .openConnection()
                            .getInputStream().use {
                        val cf = CertificateFactory.getInstance("X.509")
                        cert = cf.generateCertificate(it)
                                as X509Certificate
                    }
                    return arrayOf(cert!!)
                }

                override fun checkClientTrusted(
                        certChain: Array<X509Certificate>,
                        authType: String) {
                    if ("RSA" != authType) {
                        throw IllegalArgumentException("Fehler Auth. Type")
                    }
                }

                override fun checkServerTrusted(
                        certChain: Array<X509Certificate>,
                        authType: String) {
                    if ("RSA" != authType) {
                        throw IllegalArgumentException("Fehler Auth. Type")
                    }
                    val dn = certChain[0].subjectX500Principal.name
                    val isValid = dn.split(",").stream()
                            .anyMatch { "CN=Juergen Zimmermann" == it }
                    if (!isValid) {
                        throw IllegalArgumentException("Fehler Certificate.")
                    }
                }
            }

            init(null, arrayOf(trustManager), SecureRandom())
        }!!
    }
}

@Component
@ConfigurationProperties(prefix = "db")
// FIXME Spring Boot 2.1
// https://github.com/spring-projects/spring-boot/issues/8762
// https://github.com/spring-projects/spring-boot/issues/1254
internal data class DbProps (
    var dbname: String = "hska",
    var dbhost: String = "localhost",
    var username: String = "admin",
    var password: String? = "p",
    var authDb: String = "admin",
    var maxConnectionsPerHost: Int = 10,
    var tls: Boolean = true)
