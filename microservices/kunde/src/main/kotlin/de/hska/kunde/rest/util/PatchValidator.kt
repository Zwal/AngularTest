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

import de.hska.kunde.entity.InteresseType
import de.hska.kunde.entity.Kunde
import javax.validation.Validator

internal object PatchValidator {
    fun validate(
            kunde: Kunde,
            operations: List<PatchOperation>,
            validator: Validator
    ): Pair<Kunde, List<String>> {
        val replaceOps =
                operations.filter { "replace" == it.op }
        val replaceResult =
                replaceOps(kunde, replaceOps, validator)
        var kundeUpdated = replaceResult.first
        val violations = replaceResult.second.toMutableList()

        val addOps = operations.filter { "add" == it.op }
        val addResult = addInteressen(kundeUpdated, addOps)
        kundeUpdated = addResult.first
        violations.addAll(addResult.second)

        val removeOps = operations.filter { "remove" == it.op }
        val removeResult = removeInteressen(kundeUpdated, removeOps)
        kundeUpdated = removeResult.first
        violations.addAll(removeResult.second)

        return kundeUpdated to violations
    }

    private fun replaceOps(
            kunde: Kunde,
            ops: Collection<PatchOperation>,
            validator: Validator): Pair<Kunde, List<String>> {
        var kundeUpdated = kunde
        val violations = ops.map {
            when (it.path) {
                "/nachname" -> {
                    val result =
                            replaceNachname(kundeUpdated, it.value, validator)
                    kundeUpdated = result.first
                    result.second
                }
                "/email" -> {
                    val result = replaceEmail(kundeUpdated, it.value, validator)
                    kundeUpdated = result.first
                    result.second
                }
                else -> emptyList()
            }
        // List<List<String>  ->  List<String>
        }.flatMap { it }
        return kundeUpdated to violations
    }

    private fun replaceNachname(
            kunde: Kunde,
            nachname: String,
            validator: Validator): Pair<Kunde, List<String>> {
        val violations = validator.validateValue(Kunde::class.java,
                "nachname", nachname)
                .map { it.message }
        val kundeUpdated = if (violations.isEmpty()) {
            kunde.copy(nachname = nachname)
        } else {
            kunde
        }

        return kundeUpdated to violations
    }

    private fun replaceEmail(
            kunde: Kunde,
            email: String,
            validator: Validator): Pair<Kunde, List<String>> {
        val violations = validator.validateValue(Kunde::class.java,
                "email", email)
                .map { it.message }
        val kundeUpdated = if (violations.isEmpty()) {
            kunde.copy(email = email)
        } else {
            kunde
        }

        return kundeUpdated to violations
    }

    @Suppress("UNCHECKED_CAST")
    private fun addInteressen(kunde: Kunde, ops: Collection<PatchOperation>):
            Pair<Kunde, List<String>> {
        val addResult = ops.filter { "/interessen" == it.path }
                .map { addInteresse(it, kunde) }
        val kundeUpdated = addResult.last().first
        val violations = addResult.mapNotNull { it.second }
        return kundeUpdated to violations
    }

    private fun addInteresse(op: PatchOperation, kunde: Kunde
    ): Pair<Kunde, String?> {
        val interesseStr = op.value
        val interesse = InteresseType.build(interesseStr)
                ?: return kunde to "$interesseStr ist kein Interesse"

        val interessen = if (kunde.interessen == null)
            mutableListOf()
        else
            kunde.interessen.toMutableList()
        interessen.add(interesse)
        val kundeUpdated = kunde.copy(interessen = interessen)
        return kundeUpdated to null
    }

    @Suppress("UNCHECKED_CAST")
    private fun removeInteressen(kunde: Kunde, removeOps: List<PatchOperation>):
            Pair<Kunde, List<String>> {
        val removeResult = removeOps.filter { "/interessen" == it.path }
                .map { removeInteresse(it, kunde) }
        val kundeUpdated = removeResult.last().first
        val violations = removeResult.mapNotNull { it.second }
        return kundeUpdated to violations
    }

    private fun removeInteresse(op: PatchOperation, kunde: Kunde):
            Pair<Kunde, String?> {
        val interesseStr = op.value
        val interesse = InteresseType.build(interesseStr)
                ?: return kunde to "$interesseStr ist kein Interesse"

        val interessen = kunde.interessen?.filter { it != interesse }
        val kundeUpdated = kunde.copy(interessen = interessen)
        return kundeUpdated to null
    }
}
