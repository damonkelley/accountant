package com.damonkelley.accountant.adapters.serializers

import com.damonkelley.accountant.budget.domain.BudgetCreated
import com.damonkelley.accountant.budget.domain.BudgetRenamed
import com.damonkelley.common.result.extensions.flatMap
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.kotest.core.spec.style.FunSpec

class BudgetEventSerializerTest : FunSpec({
    listOf(
        BudgetCreated("My First Budget"),
        BudgetRenamed("My Renamed Budget"),
    ).forEach { event ->
        test("it can serialize a ${event::class.java.simpleName}") {
            val serializer = BudgetEventSerializer()

            val serializedDeserialized =
                serializer.serialize(event)
                    .flatMap { json -> serializer.deserialize(event::class.java.simpleName, json) }

            assertThat(serializedDeserialized, equalTo(event))
        }
    }
})