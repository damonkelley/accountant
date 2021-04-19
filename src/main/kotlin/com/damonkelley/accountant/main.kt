package com.damonkelley.accountant

import com.damonkelley.accountant.adapters.BudgetCommandSerializer
import com.damonkelley.accountant.adapters.BudgetEventSerializer
import com.damonkelley.accountant.adapters.EventStoreBudgetProvider
import com.damonkelley.accountant.budget.application.CreateBudgetHandler
import com.damonkelley.accountant.budget.domain.BudgetCommand
import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.infrastructure.eventstoredb.EventStoreForEventStoreDB
import com.damonkelley.accountant.eventstore.EventStoreSubscription
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString

fun main() {
    val settings = EventStoreDBConnectionString.parse("esdb://localhost:2113?tls=false")
    val client = EventStoreDBClient.create(settings)
    val eventStore = EventStoreForEventStoreDB(client)
    val repository = EventStoreBudgetProvider(eventStore)

    EventStoreSubscription(eventStore, BudgetCommandSerializer()).of("budget:commands") { trace, command: BudgetCommand ->
        when (command) {
            is CreateBudget -> CreateBudgetHandler(repository).handle(trace, command).also { println(command)}
        }
    }

    readLine()
}

