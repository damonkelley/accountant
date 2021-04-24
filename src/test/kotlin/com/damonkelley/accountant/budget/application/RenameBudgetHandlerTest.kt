package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.BudgetEvent
import com.damonkelley.accountant.budget.domain.Renamable
import com.damonkelley.accountant.budget.domain.RenameBudget
import com.damonkelley.accountant.eventsourcing.ExistingAggregateRootProvider
import com.damonkelley.accountant.trace
import com.damonkelley.accountant.tracing.EventTrace
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.kotest.core.spec.style.StringSpec
import org.junit.jupiter.api.assertDoesNotThrow
import java.util.UUID

class RenameBudgetHandlerTest : StringSpec({
    val budgetId = UUID.randomUUID()
    "it renames the budget" {
        val budget = TestBudget()
        val provider = FakeBudgetProvider(budget)

        RenameBudgetHandler(provider)
            .handle(trace(), RenameBudget(budgetId, "Renamed!"))

        assertThat(budget.name, equalTo("Renamed!"))
    }

    "budget might not exist" {
        val provider = FakeBudgetProvider(null)

        assertDoesNotThrow {
            RenameBudgetHandler(provider)
                .handle(trace(), RenameBudget(budgetId, "Renamed!"))
        }
    }
})

class FakeBudgetProvider(private val budget: Renamable?) : ExistingAggregateRootProvider<Renamable> {
    val changes: MutableList<BudgetEvent> = mutableListOf()

    override fun load(id: UUID, trace: EventTrace, block: (Renamable?) -> Renamable?): Result<Unit> {
        return block(budget)
            .let { Result.success(Unit) }
    }
}

class TestBudget(var name: String = "") : Renamable {
    override fun rename(name: String): TestBudget {
        return apply { this.name = name }
    }
}