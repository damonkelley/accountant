package com.damonkelley.accountant.budget.domain

import com.damonkelley.accountant.eventsourcing.WritableAggregateRoot

interface Createable {
    fun create(name: String): Createable
}

interface Renamable {
    fun rename(name: String): Renamable
}

class Budget(aggregateRoot: WritableAggregateRoot<BudgetEvent>) :
    Renamable,
    Createable,
    WritableAggregateRoot<BudgetEvent> by aggregateRoot {
    private lateinit var name: String

    override fun create(name: String): Budget {
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

    override fun rename(name: String): Budget {
        return apply {
            raise(BudgetRenamed(name))
        }
    }
}