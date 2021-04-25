package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.trace
import com.damonkelley.accountant.tracing.EventTrace
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.kotest.core.spec.style.FunSpec
import java.util.UUID
import kotlin.test.assertTrue

class CreateBudgetHandlerTest : FunSpec({
    val uuid = { UUID.fromString("48850a0d-2735-4cd8-9000-5d35d89adcea") }

    context("when it is successful") {
        test("it returns the trace") {
            val repository = TestRepository()
            val trace = trace()

            val result = CreateBudgetHandler(repository, uuid)
                .handle(CreateBudget("Banana Stand"), trace)

            assertThat(result.getOrThrow(), equalTo(trace))
        }

        test("it saves the budget") {
            val repository = TestRepository()
            val trace = trace()

            CreateBudgetHandler(repository, uuid)
                .handle(CreateBudget("Banana Stand"), trace)

            assertThat(repository, hasSavedBudgetWithName(uuid(), "Banana Stand"))
        }
    }

    context("save fails") {
        test("it returns the failure") {
            val repository = FailingBudgetRepository()
            val trace = trace()

            val result = CreateBudgetHandler(repository, uuid)
                .handle(CreateBudget("Banana Stand"), trace)

            assertTrue(result.isFailure)
        }
    }
})

class FailingBudgetRepository : SaveBudget {
    override fun save(aggregateRoot: Budget, trace: EventTrace): Result<Budget> {
        return Result.failure(Error("Unable to save"))
    }
}