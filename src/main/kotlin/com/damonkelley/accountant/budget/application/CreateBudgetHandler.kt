package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.BudgetEvent
import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.eventsourcing.Command
import com.damonkelley.accountant.eventsourcing.Handler
import com.damonkelley.accountant.eventsourcing.RecordableProvider
import com.damonkelley.accountant.eventsourcing.Repository

// TODO: Use a generic here so that we can maybe combine the recordable provider and repository here into a single
// argument that implements both
class CreateBudgetHandler(private val repository: Repository<Budget>, val recordableProvider: RecordableProvider<BudgetEvent>) : Handler<CreateBudget> {
    override fun handle(command: Command<CreateBudget>): Result<Unit> {
        return recordableProvider.from(command.context)
                .let { Budget.create(command.body.name, it) }
                .let { repository.save(it) }
                .let { Result.success(Unit) }
    }
}