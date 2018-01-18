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

import io.undertow.server.handlers.encoding.ContentEncodingRepository
import io.undertow.server.handlers.encoding.EncodingHandler
import io.undertow.server.handlers.encoding.GzipEncodingProvider
import io.undertow.UndertowOptions.ENABLE_HTTP2
import java.io.FileNotFoundException
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.servlet.MultipartConfigElement
import org.apache.logging.log4j.LogManager.getLogger
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.web.embedded.undertow.UndertowBuilderCustomizer
import org.springframework.boot.web.embedded.undertow
        .UndertowReactiveWebServerFactory
import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.MultipartConfigFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Description
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils.getURL

/**
 * Spring-Konfiguration fuer den EmbeddedServletContainer
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
internal interface WebServerReactiveConfig {
    /**
     * Spring Bean fuer die Undertow-Factory (mit HTTPS) erzeugen
     * @param props ConfigurationProperties fuer den Server, insbesondere TLS
     * @return Factory fuer Undertow
     */
    @Bean
    @Description("Undertow Embedded mit HTTPS")
    fun webServerFactoryCustomizer(props: ServerProps) =
        WebServerFactoryCustomizer<AbstractConfigurableWebServerFactory> {
            if (it !is UndertowReactiveWebServerFactory) {
                val zusatz =
                if (it::class.simpleName == "UndertowServletWebServerFactory")
                    ". Spring MVC statt Spring WebFlux wird genutzt."
                else
                    ""
                throw IllegalArgumentException(
                        "Falsche Klasse: ${it::class.qualifiedName}$zusatz")
            }
            setHttps(it, props)
        }

    private fun setHttps(factory: UndertowReactiveWebServerFactory,
                         props: ServerProps) {
        val port = props.ssl?.port ?: return

        val sslContext = getSslContext(props.ssl!!) ?: return
        val customizer =
            getUndertowBuilderCustomizer(port, sslContext)

        factory.builderCustomizers = listOf(customizer)
    }

    private fun getSslContext(sslProps: ServerProps.Ssl): SSLContext? {
        val keystore = KeyStore.getInstance(sslProps.keyStoreType)

        return try {
            //getURL("$CLASSPATH_URL_PREFIX${sslProps.keyStore}")
            getURL("${sslProps.keyStore}")
                    .openConnection()
                    .getInputStream().use {
                keystore.load(it, sslProps.keyStorePassword.toCharArray())
                val keyManagerFactory =
                    KeyManagerFactory.getInstance(sslProps.keyStoreProvider)
                        .apply {
                            init(keystore,
                                    sslProps.keyPassword.toCharArray())
                        }
                SSLContext.getInstance(sslProps.protocol).apply {
                    init(keyManagerFactory.keyManagers, null,
                            null)
                    LOGGER.info("SSLContext initialisiert: {}", sslProps)
                }
            }
        } catch (e: FileNotFoundException) {
            LOGGER.warn("Kein HTTPS: ${sslProps.keyStore} fehlt", e)
            null
        }
    }

    // http://undertow.io/undertow-docs/undertow-docs-1.4.0/listeners.html
    // https://github.com/undertow-io/undertow/blob/master/examples/src/...
    //         ...main/java/io/undertow/examples/http2/Http2Server.java
    // http://www.programcreek.com/java-api-examples/...
    //        ...index.php?api=org.xnio.Options
    // https://github.com/undertow-io/undertow/blob/master/core/src/...
    //         ...main/java/io/undertow/Undertow.java
    private fun getUndertowBuilderCustomizer(httpsPort: Int,
                                             sslContext: SSLContext) =
        UndertowBuilderCustomizer {
            val host = "localhost"
            it.setServerOption(ENABLE_HTTP2, true)
                    // ggf. XnioWorker und ByteBufferPool konfigurieren
                    // TODO Header Content-Security-Policy setzen
                    .addHttpsListener(httpsPort, host, sslContext)
                    .setHandler(compressionHandler(50))
        }

    private fun compressionHandler(gzipPriority: Int) =
        EncodingHandler(
            ContentEncodingRepository()
                .addEncodingHandler("gzip", GzipEncodingProvider(),
                    gzipPriority))

    /**
     * Spring Bean, um binaere Dateien hochladen zu koennen
     * @return Das Spring Bean fuer Multipart-Requests.
     */
    @Bean
    fun multipartConfigElement(): MultipartConfigElement {
        val maxUploadSize = "10MB"
        val factory = MultipartConfigFactory().apply {
            setMaxFileSize(maxUploadSize)
            setMaxRequestSize(maxUploadSize)
        }
        return factory.createMultipartConfig()
    }

    private companion object {
        val LOGGER = getLogger(WebServerReactiveConfig::class.java)
    }
}

@Component
@ConfigurationProperties(prefix = "server")
// FIXME https://github.com/spring-projects/spring-boot/issues/8762
internal data class ServerProps (var ssl: Ssl? = null) {

    @Suppress("UseDataClass")
    class Ssl {
        var keyPassword = "zimmermann"
        var keyStore = "keystore.p12"
        var keyStorePassword = "zimmermann"
        var keyStoreProvider = "SunX509"
        var keyStoreType = "PKCS12"
        var protocol = "TLSv1.2"

        var port: Int? = null
    }
}
