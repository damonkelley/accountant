package com.damonkelley.accountant.adapters

import com.damonkelley.accountant.adapters.serializers.BudgetEventSerializer
import com.damonkelley.accountant.budget.domain.BudgetCreated
import com.damonkelley.accountant.budget.domain.BudgetRenamed
import com.damonkelley.accountant.trace
import com.damonkelley.common.result.extensions.flatMap
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BudgetEventMapperTest : FunSpec({
    listOf(
        BudgetCreated("Banana Stand"),
        BudgetRenamed("🍌 Banana Stand")
    ).forEach { budgetEvent ->
        val mapper = BudgetEventMapper(BudgetEventSerializer())

        val mappedEvent = mapper.toEvent(budgetEvent, trace())
            .flatMap { eventStoreEvent -> mapper.fromEvent(eventStoreEvent) }

        assertThat(mappedEvent, equalTo(Result.success(budgetEvent)))
    }
})
