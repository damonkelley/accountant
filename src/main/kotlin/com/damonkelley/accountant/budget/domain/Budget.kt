package com.damonkelley.accountant.budget.domain

import com.damonkelley.accountant.eventsourcing.WritableAggregateRoot

class Budget(aggregateRoot: WritableAggregateRoot<BudgetEvent>) : WritableAggregateRoot<BudgetEvent> by aggregateRoot {
    private lateinit var name: String

    fun create(name: String): Budget {
        raise(BudgetCreated(name))

        return this
    }

    init {
        facts {
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