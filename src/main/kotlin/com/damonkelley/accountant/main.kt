package com.damonkelley.accountant

import com.damonkelley.accountant.budget.adapters.EventStoreBudgetRepository
import com.damonkelley.accountant.budget.adapters.serializers.BudgetCommandSerializer
import com.damonkelley.accountant.budget.application.CreateBudgetHandler
import com.damonkelley.accountant.budget.application.RenameBudgetHandler
import com.damonkelley.accountant.budget.domain.BudgetCommand
import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.budget.domain.RenameBudget
import com.damonkelley.accountant.eventstore.Subscription
import com.damonkelley.accountant.infrastructure.eventstoredb.EventStoreClient
import com.damonkelley.accountant.infrastructure.eventstoredb.PersistentSubscriptionsClient
import java.util.UUID
import com.eventstore.dbclient.EventStoreDBClient as ESDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString as ESDBConnectionSting
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient as ESDBPersistentSubscriptionClient

fun main() {
    val settings = ESDBConnectionSting.parse("esdb://localhost:2113?tls=false")
    val client = ESDBClient.create(settings)
    val persistentSubscriptionClient =
        PersistentSubscriptionsClient(
            "app",
            ESDBPersistentSubscriptionClient.create(settings)
        )

    val eventStore = EventStoreClient(client)
    val repository = EventStoreBudgetRepository(eventStore)

    Subscription(persistentSubscriptionClient, BudgetCommandSerializer()).of("budget:commands") { trace, command: BudgetCommand->
        when (command) {
            is CreateBudget -> CreateBudgetHandler(repository, UUID::randomUUID).handle(command, trace).map { }
            is RenameBudget -> RenameBudgetHandler(repository).handle(command, trace).map { }
        }
    }

    readLine()
}