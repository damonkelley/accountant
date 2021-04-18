package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.eventsourcing.Handler

class CreateBudgetHandler(private val provider: NewBudgetProvider) : Handler<CreateBudget> {
    override fun handle(command: CreateBudget): Result<Unit> {
        return provider
                .new { budget -> budget.create(command.name) }
                .let { Result.success(Unit) }
    }
}