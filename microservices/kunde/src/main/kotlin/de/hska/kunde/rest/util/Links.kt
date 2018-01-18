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
package de.hska.kunde.rest.util

import java.net.URI

// Vereinfachte Variante fuer Spring HATEOAS (auf Basis von Spring MVC)
// https://github.com/spring-projects/spring-boot/blob/master/...
//       ...spring-boot-autoconfigure/src/main/java/org/springframework/boot...
//       .../autoconfigure/hateoas/HypermediaAutoConfiguration.java

//"_links" : {
//    "self" : {
//        "href" : "https://localhost:8444/12345678-1234-1234-1234-123456789012"
//    },
//    "list" : {
//        "href" : "https://localhost:8444"
//    },
//    "add" : {
//        "href" : "https://localhost:8444"
//    },
//    "update" : {
//        "href" : "https://localhost:8444"
//    },
//    "remove" : {
//        "href" : "https://localhost:8444/12345678-1234-1234-1234-123456789012"
//    }
//}

typealias SingleLink = Map<String, String>
val HREF = "href"
val SELF = "self"
val LIST = "list"
val ADD = "add"
val UPDATE = "update"
val REMOVE = "remove"

typealias SingleLinks = Map<String, SingleLink>

@Suppress("unused")
class SingleLinksBuilder (selfUri: URI) {
    private val selfUriStr: String = selfUri.toString()
    private val baseUri by lazy {
        val indexLastSlash = selfUriStr.lastIndexOf('/')
        selfUriStr.substring(0, indexLastSlash)
    }
    private var listUri: String = baseUri
    private var addUri: String = baseUri
    private var updateUri: String = baseUri
    private var removeUri: String = selfUriStr

    fun build() =
        mapOf(SELF to mapOf(HREF to selfUriStr),
            LIST to mapOf(HREF to listUri),
            ADD to mapOf(HREF to addUri),
            UPDATE to mapOf(HREF to updateUri),
            REMOVE to mapOf(HREF to removeUri))

    fun list(uri: URI): SingleLinksBuilder {
        listUri = uri.toString()
        return this
    }

    fun add(uri: URI): SingleLinksBuilder {
        addUri = uri.toString()
        return this
    }

    fun updateUri(uri: URI): SingleLinksBuilder {
        listUri = uri.toString()
        return this
    }

    fun removeUri(uri: URI): SingleLinksBuilder {
        listUri = uri.toString()
        return this
    }
}

fun URI.singleLinks() = SingleLinksBuilder(this).build()

//"links" : [
//    {
//        "rel" : "self",
//        "href" : "https://localhost:8444/a374b3ca-434c-48ee-baa9-6f9689b969ef"
//    }
//]

typealias ItemLinks = List<Map<String, String>>
val REL = "rel"

fun URI.itemLinks(id: String): List<Map<String, String>> {
    val scheme = this.scheme
    val host = this.host
    val port = this.port
    val path = this.path
    val uri = URI(scheme, null, host, port, path, null, null)
    return listOf(
            mapOf(REL to SELF),
            mapOf(HREF to "$uri$id"))
}
