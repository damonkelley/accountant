package com.damonkelley.accountant

import com.damonkelley.accountant.adapters.EventStoreBudgetRepository
import com.damonkelley.accountant.budget.application.CreateBudgetHandler
import com.damonkelley.accountant.budget.domain.BudgetCommand
import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.infrastructure.Context
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString

fun main() {
//    val settings = EventStoreDBConnectionString.parse("")
//    val client = EventStoreDBClient.create(settings)
//    val eventStore = EventStore(client)
//
//    EventStoreSubscription(eventStore).of("budget:commands") { context: Context, command: CreateBudget ->
//        val repository = EventStoreBudgetRepository(EventStore(client))
//        CreateBudgetHandler(repository).handle(command)
//    }
}