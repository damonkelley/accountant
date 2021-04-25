package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.eventsourcing.SimpleAggregateRoot
import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.common.result.extensions.flatMap
import java.util.UUID

class CreateBudgetHandler(private val repository: SaveBudget, val UUIDProvider: () -> UUID) {
    fun handle(command: CreateBudget, trace: EventTrace): Result<EventTrace> {
        return Result.success(Budget(SimpleAggregateRoot(id = UUIDProvider())).create(command.name))
            .flatMap { repository.save(it, trace) }
            .map { trace }
    }
}