package com.damonkelley.accountant

import com.damonkelley.accountant.budget.adapters.serializers.BudgetCommandSerializer
import com.damonkelley.accountant.eventstore.Subscription
import com.damonkelley.accountant.infrastructure.eventstoredb.EventStoreClient
import com.damonkelley.accountant.infrastructure.eventstoredb.PersistentSubscriptionsClient
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

    Subscription(persistentSubscriptionClient, BudgetCommandSerializer()).of("budget:commands") { _, _->
        Result.success(Unit)
    }

    readLine()
}

