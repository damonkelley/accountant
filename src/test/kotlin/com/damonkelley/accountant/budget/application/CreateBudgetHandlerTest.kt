package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.trace
import com.damonkelley.accountant.tracing.EventTrace
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import io.kotest.core.spec.style.FunSpec
import java.util.UUID

class CreateBudgetHandlerTest : FunSpec({
    val uuid = { UUID.fromString("48850a0d-2735-4cd8-9000-5d35d89adcea") }

    context("when it is successful") {
        test("it returns the trace") {
            val repository = FakeBudgetRepository()
            val trace = trace()

            val result = CreateBudgetHandler(repository, uuid)
                .handle(CreateBudget("Banana Stand"), trace)

            assertThat(result.getOrThrow(), equalTo(trace))
        }

        test("it saves the budget") {
            val repository = FakeBudgetRepository()
            val trace = trace()

            CreateBudgetHandler(repository, uuid)
                .handle(CreateBudget("Banana Stand"), trace)

            assertThat(repository, hasBudgetWithName(uuid(), "Banana Stand"))
        }
    }

    context("save fails") {
        test("it returns the failure") {
            val repository = FailingBudgetRepository()
            val trace = trace()

            val result = CreateBudgetHandler(repository, uuid)
                .handle(CreateBudget("Banana Stand"), trace)

            assert(result.isFailure)
        }
    }
})

fun hasBudgetWithName(id: UUID, name: String): Matcher<FakeBudgetRepository> {
    return has("budget saved with name", { it.saved[id]?.name }, equalTo(name))
}

class FakeBudgetRepository : SaveBudget {
    val saved = mutableMapOf<UUID, Budget>()
    override fun save(aggregateRoot: Budget, trace: EventTrace): Result<Budget> {
        saved[aggregateRoot.id] = aggregateRoot
        return Result.success(aggregateRoot)
    }
}

class FailingBudgetRepository : SaveBudget {
    override fun save(aggregateRoot: Budget, trace: EventTrace): Result<Budget> {
        return Result.failure(Error("Unable to save"))
    }
}