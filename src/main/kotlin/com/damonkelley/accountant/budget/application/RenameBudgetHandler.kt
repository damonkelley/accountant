package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.RenameBudget
import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.common.result.extensions.flatMap

class RenameBudgetHandler<Repository>(private val repository: Repository) where Repository : SaveBudget, Repository : LoadBudget {
    fun handle(command: RenameBudget, trace: EventTrace): Result<EventTrace> {
        return repository.load(command.budgetId)
            .map { it.rename(command.name) }
            .flatMap { repository.save(it, trace) }
            .map { trace }
    }
}