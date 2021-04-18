package com.damonkelley.accountant

import com.damonkelley.accountant.adapters.EventStoreBudgetProvider
import com.damonkelley.accountant.budget.application.CreateBudgetHandler
import com.damonkelley.accountant.budget.domain.BudgetCommand
import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.infrastructure.eventstoredb.EventStoreForEventStoreDB
import com.damonkelley.accountant.eventstore.EventStoreSubscription
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString

fun main() {
    val settings = EventStoreDBConnectionString.parse("")
    val client = EventStoreDBClient.create(settings)
    val eventStore = EventStoreForEventStoreDB(client)

    EventStoreSubscription<BudgetCommand>(eventStore).of("budget:commands") { command: BudgetCommand ->
        val repository = EventStoreBudgetProvider(eventStore)

        when (command) {
            is CreateBudget -> CreateBudgetHandler(repository).handle(command)
        }
    }
}

