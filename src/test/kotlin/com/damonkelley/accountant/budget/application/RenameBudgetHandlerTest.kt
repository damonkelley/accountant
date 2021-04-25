package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.RenameBudget
import com.damonkelley.accountant.eventsourcing.SimpleAggregateRoot
import com.damonkelley.accountant.trace
import com.natpryce.hamkrest.assertion.assertThat
import io.kotest.core.spec.style.FunSpec
import java.util.UUID
import kotlin.test.assertTrue

class RenameBudgetHandlerTest : FunSpec({
    val budgetId = UUID.fromString("e6e70de0-7ed3-413b-afeb-2f02ce9c4464")

    context("when the budget does not exist") {
        test("it returns the failure") {
            val repository = TestRepository()

            val result = RenameBudgetHandler(repository)
                .handle(RenameBudget(budgetId, "🍌 Banana Stand"), trace())

            assertTrue(result.isFailure, "The handler did not fail")
        }
    }

    context("when the budget exists") {
        test("it will rename and save the budget") {
            val repository = TestRepository(Budget(SimpleAggregateRoot(budgetId)))

            RenameBudgetHandler(repository)
                .handle(RenameBudget(budgetId, "🍌 Banana Stand"), trace())

            assertThat(repository, hasSavedBudgetWithName(budgetId, "🍌 Banana Stand"))
        }
    }
})