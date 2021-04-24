package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.Renamable
import com.damonkelley.accountant.budget.domain.RenameBudget
import com.damonkelley.accountant.eventsourcing.ExistingAggregateRootProvider
import com.damonkelley.accountant.eventsourcing.Handler
import com.damonkelley.accountant.tracing.EventTrace

class RenameBudgetHandler(private val provider: ExistingAggregateRootProvider<Renamable>) : Handler<RenameBudget> {
    override fun handle(trace: EventTrace, command: RenameBudget): Result<Unit> {
        provider.load(command.budgetId, trace) {
            it?.rename(command.name)
        }
        return Result.success(Unit)
    }
}