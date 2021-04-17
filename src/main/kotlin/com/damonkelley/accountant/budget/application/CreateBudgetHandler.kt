package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.eventsourcing.Handler

// TODO: Use a generic here so that we can maybe combine the recordable provider and repository here into a single
// argument that implements both
class CreateBudgetHandler(private val provider: NewBudgetProvider) : Handler<CreateBudget> {
    override fun handle(command: CreateBudget): Result<Unit> {
        return provider
                .new { budget -> budget.create(command.name) }
                .let { Result.success(Unit )}
    }
}