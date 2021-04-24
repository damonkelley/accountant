package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.budget.domain.Createable
import com.damonkelley.accountant.eventsourcing.Handler
import com.damonkelley.accountant.eventsourcing.NewAggregateRootProvider
import com.damonkelley.accountant.tracing.EventTrace

class CreateBudgetHandler(private val provider: NewAggregateRootProvider<Createable>) : Handler<CreateBudget> {
    override fun handle(trace: EventTrace, command: CreateBudget): Result<Unit> {
        return provider
                .new(trace) { budget -> budget.create(command.name) }
                .let { Result.success(Unit) }
    }
}