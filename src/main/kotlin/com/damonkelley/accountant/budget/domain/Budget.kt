package com.damonkelley.accountant.budget.domain

import com.damonkelley.accountant.account.domain.Account
import com.damonkelley.accountant.eventsourcing.AggregateRoot

class Budget(private val aggregateRoot: AggregateRoot<BudgetEvent>) :
    AggregateRoot<BudgetEvent> by aggregateRoot {
    var name: String = ""

    init {
        aggregateRoot.facts {
            when (it) {
                is BudgetCreated -> apply(it)
                is BudgetRenamed -> apply(it)
            }
        }
    }

    fun create(name: String): Budget {
        return apply { aggregateRoot.raise(apply(BudgetCreated(name))) }
    }

    fun rename(name: String): Budget {
        return apply {
            aggregateRoot.raise(apply(BudgetRenamed(name)))
        }
    }

    fun addAccount(name: String): Account {
        return Account().create(name = name, budgetId = id)
    }

    private fun apply(event: BudgetCreated): BudgetCreated {
        name = event.name
        return event
    }

    private fun apply(event: BudgetRenamed): BudgetRenamed {
        name = event.name
        return event
    }
}