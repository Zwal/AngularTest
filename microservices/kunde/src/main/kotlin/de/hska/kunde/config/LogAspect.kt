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

import java.util.Arrays
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager.getLogger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

/**
 * Logging von Methodenaufrufen durch AspectJ
 * @author [Juergen Zimmermann](mailto:Juergen.Zimmermann@HS-Karlsruhe.de)
 */
@Aspect
@Component
@Suppress("TooManyFunctions")
internal class LogAspect {
    @Around("execution(* de.hska.*.rest.*Handler.*(..))" +
            " || execution(* de.hska.*.service.*Service*.*(..))" +
            " || execution(* de.hska.*.config.security.*Service*.*(..))")
    @Suppress("UselessPostfixExpression")
    fun logMethod(joinPoint: ProceedingJoinPoint): Any {
        var result: Any? = null
        with (joinPoint) {
            val clazz = target.javaClass
            val log = LOGGER_MAP.computeIfAbsent(clazz) { getLogger(clazz) }

            if (!log.isDebugEnabled) {
                return proceed()
            }

            val methodName = signature.name

            // Methodenaufruf protokollieren
            logMethodBegin(log, methodName, args)

            // Eigentlicher Methodenaufruf
            result = proceed()

            // Ende der eigentlichen Methode protokollieren
            logMethodEnd(log, methodName, result)
        }
        return result!!
    }

    @Suppress("NestedBlockDepth")
    private fun logMethodBegin(log: Logger, methodName: String,
                               args: Array<Any?>?) {
        var argsStr = ""
        args?.let {
            val sb = StringBuilder(STRING_BUILDER_INITIAL_SIZE)
            val anzahlArgs = args.size
            with (sb) {
                append(": ")
                for (i in 0 until anzahlArgs) {
                    val argStr =
                        if (args[i] == null) "null" else toString(args[i]!!)
                    append(argStr)
                    append(PARAM_SEPARATOR)
                }
                val laenge = length
                delete(laenge - PARAM_SEPARATOR_LENGTH, laenge - 1)
            }
            argsStr = sb.toString()
        }
        log.debug("$methodName BEGINN$argsStr")
    }

    private fun logMethodEnd(log: Logger, methodName: String,
                             result: Any?) {
        val endStr = if (result == null)
            "$methodName ENDE"
        else
            "$methodName ENDE: ${toString(result)}"
        log.debug(endStr)
    }

    private fun toString(obj: Any): String {
        if (obj is Collection<*>) {
            // die Elemente nur bei kleiner Anzahl ausgeben
            // sonst nur die Anzahl der Elemente
            val anzahl = obj.size
            return if (anzahl > MAX_ELEM) {
                "$COUNT${obj.size}"
            } else obj.toString()
        }

        return if (obj.javaClass.isArray) {
            // Array in String konvertieren: Element fuer Element
            arrayToString(obj)
        } else obj.toString()

        // Objekt, aber keine Collection und kein Array
    }

    private fun arrayToString(obj: Any): String {
        val componentClass = obj.javaClass.componentType
        if (!componentClass.isPrimitive) {
            return arrayOfObject(obj)
        }

        // Array von primitiven Werten: byte, short, int, long, float, double,
        // boolean, char
        val className = componentClass.name
        return arrayOfPrimitive(obj, className)
    }

    private fun arrayOfObject(obj: Any): String {
        @Suppress("UNCHECKED_CAST")
        val arr = obj as Array<Any>
        return if (arr.size > MAX_ELEM) {
            "$COUNT${arr.size}"
        } else Arrays.toString(arr)
    }

    private fun arrayOfPrimitive(obj: Any, className: String) =
        when (className) {
            "byte" -> arrayOfByte(obj)
            "short" -> arrayOfShort(obj)
            "int" -> arrayOfInt(obj)
            "long" -> arrayOfLong(obj)
            "float" -> arrayOfFloat(obj)
            "double" -> arrayOfDouble(obj)
            "boolean" -> arrayOfBoolean(obj)
            "char" -> arrayOfChar(obj)
            else -> "<<UNKNOWN PRIMITIVE ARRAY>>"
    }

    private fun arrayOfByte(obj: Any): String {
        val arr = obj as ByteArray
        return if (arr.size > MAX_ELEM) {
            "$COUNT${arr.size}"
        } else Arrays.toString(arr)
    }

    private fun arrayOfShort(obj: Any): String {
        val arr = obj as ShortArray
        return if (arr.size > MAX_ELEM) {
            "$COUNT${arr.size}"
        } else Arrays.toString(arr)
    }

    private fun arrayOfInt(obj: Any): String {
        val arr = obj as IntArray
        return if (arr.size > MAX_ELEM) {
            "$COUNT${arr.size}"
        } else Arrays.toString(arr)
    }

    private fun arrayOfLong(obj: Any): String {
        val arr = obj as LongArray
        return if (arr.size > MAX_ELEM) {
            "$COUNT${arr.size}"
        } else Arrays.toString(arr)
    }

    private fun arrayOfFloat(obj: Any): String {
        val arr = obj as FloatArray
        return if (arr.size > MAX_ELEM) {
            "$COUNT${arr.size}"
        } else Arrays.toString(arr)
    }

    private fun arrayOfDouble(obj: Any): String {
        val arr = obj as DoubleArray
        return if (arr.size > MAX_ELEM) {
            "$COUNT${arr.size}"
        } else Arrays.toString(arr)
    }

    private fun arrayOfBoolean(obj: Any): String {
        val arr = obj as BooleanArray
        return if (arr.size > MAX_ELEM) {
            "$COUNT${arr.size}"
        } else Arrays.toString(arr)
    }

    private fun arrayOfChar(obj: Any): String {
        val arr = obj as CharArray
        return if (arr.size > MAX_ELEM) {
            "$COUNT${arr.size}"
        } else Arrays.toString(arr)
    }

    private companion object {
        val COUNT = "Anzahl: "
        // bei Collections wird ab 5 Elementen nur die Anzahl ausgegeben
        val MAX_ELEM = 4

        val STRING_BUILDER_INITIAL_SIZE = 64
        val PARAM_SEPARATOR = ", "
        val PARAM_SEPARATOR_LENGTH = PARAM_SEPARATOR.length

        val LOGGER_MAP = HashMap<Class<*>, Logger>()
    }
}
