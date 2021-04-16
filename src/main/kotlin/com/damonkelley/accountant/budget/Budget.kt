package com.damonkelley.accountant.budget

import com.damonkelley.accountant.eventsourcing.AggregateRoot
import com.damonkelley.accountant.eventsourcing.Handler

import java.util.UUID


class CreateBudgetHandler : Handler<CreateBudget, BudgetEvent> {
    override fun handle(command: CreateBudget): Budget {
        return Budget.create(command.name)
    }
}

class Budget(
        override val id: UUID,
        facts: List<BudgetEvent> = emptyList(),
        override val changes: List<BudgetEvent> = emptyList()
) : AggregateRoot<BudgetEvent> {
    lateinit var name: String

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
            val event = BudgetCreated(UUID.randomUUID(), name)
            return Budget(event.id, changes = listOf(event))
        }
    }
}

sealed class BudgetEvent
data class BudgetCreated(val id: UUID, val name: String) : BudgetEvent()

sealed class BudgetCommands
data class CreateBudget(val name: String) : BudgetCommands()