@file:Suppress("StringLiteralDuplication")
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

package de.hska.registry.config

import org.springframework.boot.Banner
import org.springframework.boot.SpringBootVersion
import org.springframework.core.SpringVersion
import org.springframework.security.core.SpringSecurityCoreVersion

@Suppress("MagicNumber")
internal object Settings {
    private val VERSION = "1.0"
    private val PORT = 8761
    val DEV_PROFILE = "dev"

    val BANNER = Banner { _, _, out ->
        out.println("""
            |    ______                __
            |   / ____/_  __________  / /______ _
            |  / __/ / / / / ___/ _ |/ //_/ __ `/
            | / /___/ /_/ / /  /  __/ ,< / /_/ /
            |/_____/|__,_/_/   |___/_/|_||__,_/
            |
            |Version           $VERSION
            |Spring Boot       ${SpringBootVersion.getVersion()}
            |Spring Security   ${SpringSecurityCoreVersion.getVersion()}
            |Spring Framework  ${SpringVersion.getVersion()}
            |JDK               ${System.getProperty("java.version")}
            |""".trimMargin("|"))
    }

    private val appName by lazy {
        val pkgName = Settings::class.java.`package`.name
        val parentPkgName =
                pkgName.substring(0, pkgName.lastIndexOf('.'))
        parentPkgName.substring(parentPkgName.lastIndexOf('.') + 1)
    }

    val PROPS = hashMapOf(
        "server.port" to PORT,
        "error.whitelabel.enabled" to false,
        "spring.application.name" to appName,
        "logging.path" to "build",

        "management.security.enabled" to false,
        "endpoints.shutdown.web.enabled" to true,

        // https://github.com/spring-cloud/spring-cloud-netflix/blob/...
        // ...master/spring-cloud-netflix-eureka-client/src/main/java/...
        // ...org/springframework/cloud/netflix/eureka/...
        // ...EurekaInstanceConfigBean.java
        "eureka.instance.hostname" to "localhost",
        "eureka.instance.homePageUrl" to
                "https://\${eureka.instance.hostname}:\${server.port}/",
        "eureka.instance.statusPageUrl" to
                "https://\${eureka.instance.hostname}:\${server.port}/admin/info",
        //"eureka.instance.securePort" to "${server.port}"
        //"eureka.instance.securePortEnabled" to true
        //"eureka.instance.nonSecurePortEnabled" to false
        //"eureka.instance.secureVirtualHostName" to
        //        "${spring.application.name}"
        //"eureka.instance.metadataMap.hostname" to
        //        "${eureka.instance.hostname}"
        //"eureka.instance.metadataMap.securePort" to "${server.port}"

        // Eureka-Server nicht bei sich selbst registrieren
        // https://github.com/spring-cloud/spring-cloud-netflix/blob/...
        // ...master/spring-cloud-netflix-eureka-client/src/main/java/...
        // ...org/springframework/cloud/netflix/eureka/...
        // ...EurekaClientConfigBean.java
        "eureka.client.registerWithEureka" to false,
        "eureka.client.fetchRegistry" to false,
        "eureka.client.server.waitTimeInMsWhenSyncEmpty" to 0,
        "eureka.client.serviceUrl.defaultZone" to
                "http://\${eureka.instance.hostname}:\${server.port}/eureka/",
        "eureka.datacenter" to "default",
        "eureka.environment" to "test",

        // Dashboard nutzt Freemarker
        "spring.thymeleaf.enabled" to false
    )
}
