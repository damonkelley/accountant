package com.damonkelley.accountant.budget.domain

import com.damonkelley.accountant.eventsourcing.AggregateRoot
import com.damonkelley.accountant.eventsourcing.Recordable

import java.util.UUID

class Budget(val id: UUID, override val recording: Recordable<BudgetEvent>): AggregateRoot<BudgetEvent> {
    private lateinit var name: String

    init {
        recording.replayFacts {
            when (it) {
                is BudgetCreated -> apply(it)
            }
        }
    }

    private fun apply(event: BudgetCreated): BudgetCreated {
        name = event.name
        return event
    }

    companion object {
        fun create(name : String, recording: Recordable<BudgetEvent>): Budget {
            val event = BudgetCreated(name)
            return Budget(id = UUID.randomUUID(), recording.record(event))
        }
    }
}

sealed class BudgetEvent
data class BudgetCreated(val name: String) : BudgetEvent()

sealed class BudgetCommands
data class CreateBudget(val name: String) : BudgetCommands()