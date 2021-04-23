package com.damonkelley.accountant.adapters.serializers

import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.budget.domain.RenameBudget
import com.damonkelley.common.result.extensions.flatMap
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.kotest.core.spec.style.FunSpec
import java.util.UUID

class BudgetCommandSerializerTest : FunSpec({
    listOf(
        CreateBudget("My First Budget"),
        RenameBudget(budgetId = UUID.randomUUID(), "🍌 Banana Stand"),
    ).forEach { event ->
        test("it can serialize a ${event::class.java.simpleName}") {
            val serializer = BudgetCommandSerializer()

            val serializedDeserialized =
                serializer.serialize(event)
                    .flatMap { json -> serializer.deserialize(event::class.java.simpleName, json) }

            assertThat(serializedDeserialized, equalTo(event))
        }
    }
})
