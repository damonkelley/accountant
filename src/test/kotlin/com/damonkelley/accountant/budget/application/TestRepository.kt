package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.tracing.EventTrace
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import java.util.UUID

class TestRepository(budget: Budget? = null) : LoadBudget, SaveBudget {
    val saved: MutableMap<UUID, Budget> = mutableMapOf()

    init {
        budget?.let { saved[it.id] = it }
    }

    override fun load(id: UUID): Result<Budget> {
        return saved[id]?.let { Result.success(it) } ?: Result.failure(Error("Budget was not found"))
    }

    override fun save(aggregateRoot: Budget, trace: EventTrace): Result<Budget> {
        saved[aggregateRoot.id] = aggregateRoot
        return Result.success(aggregateRoot)
    }
}

fun hasSavedBudgetWithName(budgetId: UUID, name: String): Matcher<TestRepository> {
    return has("budget saved with name", { it.saved[budgetId]?.name }, equalTo(name))
}