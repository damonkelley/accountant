package com.damonkelley.accountant.infrastructure.eventstoredb

import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.eventstore.EventStoreSubscriber
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.eventstore.dbclient.NackAction
import com.eventstore.dbclient.PersistentSubscription
import com.eventstore.dbclient.PersistentSubscriptionListener
import com.eventstore.dbclient.ResolvedEvent

class EventStoreDBPersistentSubscriptionEventStoreClient(
    private val group: String,
    private val client: EventStoreDBPersistentSubscriptionsClient
) : EventStoreSubscriber {
    override fun subscribe(streamId: String, onEvent: (EventStore.Event) -> Result<Unit>) {
        client.subscribe(streamId, group, object : PersistentSubscriptionListener() {
            override fun onError(subscription: PersistentSubscription, throwable: Throwable) {
                println("Error: ${subscription.subscriptionId} | $throwable")
            }

            override fun onEvent(subscription: PersistentSubscription, event: ResolvedEvent) {
                println("Subscription: Received [${event.event.eventType}] ${String(event.event.eventData)}")
                onEvent(event.toEventStoreEvent())
                    .fold(
                        { subscription.ack(event) },
                        { subscription.nack(NackAction.Park, it.message, event) }
                    )
            }

            override fun onCancelled(subscription: PersistentSubscription) {
                println("Cancelled: ${subscription.subscriptionId}")
            }
        })
    }

}