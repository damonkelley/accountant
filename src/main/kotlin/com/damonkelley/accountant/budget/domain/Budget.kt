package com.damonkelley.accountant.budget.domain

import com.damonkelley.accountant.eventsourcing.AggregateRoot

import java.util.UUID

class Budget(val id: UUID, val recording: AggregateRoot<BudgetEvent>) : AggregateRoot<BudgetEvent> by recording {
    private lateinit var name: String

    fun create(name: String): Budget {
        record(BudgetCreated(name))

        return this
    }

    init {
        replayFacts {
            when (it) {
                is BudgetCreated -> apply(it)
            }
        }
    }

    private fun apply(event: BudgetCreated): BudgetCreated {
        name = event.name
        return event
    }
}

sealed class BudgetEvent
data class BudgetCreated(val name: String) : BudgetEvent()

sealed class BudgetCommands
data class CreateBudget(val name: String) : BudgetCommands()