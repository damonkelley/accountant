package com.damonkelley.accountant.budget.domain

import com.damonkelley.accountant.eventsourcing.AggregateRoot
import com.damonkelley.accountant.eventsourcing.ReadableAggregateRoot

interface Createable {
    fun create(name: String): Createable
}

interface Renamable {
    fun rename(name: String): Renamable
}

class Budget(val aggregateRoot: AggregateRoot<BudgetEvent>) :
    Renamable,
    Createable,
    ReadableAggregateRoot<BudgetEvent> by aggregateRoot {
    var name: String = ""

    override fun create(name: String): Budget {
        aggregateRoot.raise(apply(BudgetCreated(name)))

        return this
    }

    init {
        aggregateRoot.facts {
            when (it) {
                is BudgetCreated -> apply(it)
                else -> Unit
            }
        }
    }

    private fun apply(event: BudgetCreated): BudgetCreated {
        name = event.name
        return event
    }

    override fun rename(name: String): Budget {
        return apply {
            aggregateRoot.raise(BudgetRenamed(name))
        }
    }
}