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

import org.springframework.boot.Banner
import org.springframework.boot.SpringBootVersion
import org.springframework.core.SpringVersion
import org.springframework.security.core.SpringSecurityCoreVersion

internal object Settings {
    const val DEV = "dev"
    private val VERSION = "1.0"
    private val EUREKA_PORT = 8761

    val BANNER = Banner { _, _, out ->
        out.println("""
            |       __                                    _____
            |      / /_  _____  _________ ____  ____     /__  /
            | __  / / / / / _ \/ ___/ __ `/ _ \/ __ \      / /
            |/ /_/ / /_/ /  __/ /  / /_/ /  __/ / / /     / /___
            |\____/\__,_/\___/_/   \__, /\___/_/ /_/     /____(_)
            |                     /____/
            |
            |(C) Juergen Zimmermann, Hochschule Karlsruhe
            |Version          $VERSION
            |Spring Boot      ${SpringBootVersion.getVersion()}
            |Spring Security  ${SpringSecurityCoreVersion.getVersion()}
            |Spring Framework ${SpringVersion.getVersion()}
            |JDK              ${System.getProperty("java.version")}
            |""".trimMargin("|"))
    }

    private val parentPkgName by lazy {
        val pkgName = Settings::class.java.`package`.name
        pkgName.substring(0, pkgName.lastIndexOf('.'))
    }
    private val appName =
        parentPkgName.substring(parentPkgName.lastIndexOf('.') + 1)

    val PROPS = mapOf(
        "spring.application.name" to appName,
        "spring.application.version" to VERSION,
        "spring.profiles.default" to "prod",
        // Functional bean definition Kotlin DSL
        //"context.initializer.classes" to "$parentPkgName.BeansInitializer",

        "spring.jackson.default-property-inclusion" to "non_null",
        //"spring.jackson.date-format" to "yyyy-MM-dd",

        // -Dreactor.trace.operatorStacktrace=true
        "spring.reactor.stacktrace-mode.enabled" to true,

        "endpoints.default.jmx.enabled" to true,
        "endpoints.default.web.enabled" to true,
        "endpoints.shutdown.jmx.enabled" to true,
        "endpoints.shutdown.web.enabled" to true,

        "spring.cloud.config.username" to "admin",
        "spring.cloud.config.password" to "p",

        // Eureka-Server lokalisieren
        // https://github.com/spring-cloud/spring-cloud-netflix/blob/master/...
        // ...spring-cloud-netflix-eureka-client/src/main/java/org/...
        // ...springframework/cloud/netflix/eureka/EurekaClientConfigBean.java
        //eureka.client.securePortEnabled" to true,
        "eureka.client.serviceUrl.defaultZone" to
                "http://localhost:$EUREKA_PORT/eureka/",

        // FIXME https://github.com/spring-projects/spring-boot/issues/7972
        //eureka.instance.securePort" to "\${server.httpsPort}",
        //eureka.instance.securePortEnabled" to true,
        //eureka.instance.nonSecurePortEnabled" to false,
        //eureka.instance.metadataMap.instanceId" to
        //     "\${vcap.application.instance_id:\${spring.application.name}:" +
        //     "\${spring.application.instance_id:\${server.securePort}}}",

        //"eureka.instance.statusPageUrl" to
        //    "https://localhost/\${management.endpoints.web.base-path}/info",
        //"eureka.instance.healthCheckUrl" to
        //    "https://localhost/\${management.endpoints.web.base-path}/health",

        // https://github.com/spring-cloud/spring-cloud-netflix/blob/master/...
        // ...spring-cloud-netflix-eureka-client/src/main/java/org/...
        // ...springframework/cloud/netflix/eureka/EurekaInstanceConfigBean.java
        // Evtl. generierter Rechnername bei z.B. Docker
        "eureka.instance.preferIpAddress" to true,

        "spring.data.mongodb.repositories.enabled" to false,
        "spring.data.mongodb.database" to "hska")

        //"server.error.whitelabel.enabled" to false,
        //"spring.devtools.restart.trigger-file=" to "/restart.txt",
}
