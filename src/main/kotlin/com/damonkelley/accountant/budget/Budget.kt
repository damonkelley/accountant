package com.damonkelley.accountant.budget

import com.damonkelley.accountant.eventsourcing.AggregateRoot
import com.damonkelley.accountant.eventsourcing.Handler
import com.damonkelley.accountant.eventsourcing.Repository

import java.util.UUID


class CreateBudgetHandler(val repository: Repository<Budget>) : Handler<CreateBudget> {
    override fun handle(command: CreateBudget): Result<Unit> {
        Budget.create(command.name)
                .let { repository.save(it) }

        return Result.success(Unit)
    }
}

class Budget(
        override val id: UUID,
        facts: List<BudgetEvent> = emptyList(),
        override val changes: List<BudgetEvent> = emptyList()
) : AggregateRoot<BudgetEvent> {
    private lateinit var name: String

    // TODO: How could this be refactored to be immutable?
    init {
        (facts + changes).forEach {
            when (it) {
                is BudgetCreated -> apply(it)
            }
        }
    }

    private fun apply(event: BudgetCreated) {
        name = event.name
    }

    companion object {
        fun create(name: String): Budget {
            val event = BudgetCreated(name)
            return Budget(UUID.randomUUID(), changes = listOf(event))
        }
    }
}

sealed class BudgetEvent
data class BudgetCreated(val name: String) : BudgetEvent()

sealed class BudgetCommands
data class CreateBudget(val name: String) : BudgetCommands()