package com.damonkelley.accountant.infrastructure.eventstoredb

import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.eventstore.Subscriber
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.Subscription
import com.eventstore.dbclient.SubscriptionListener

class EventStoreClient(private val client: EventStoreDBClient) : EventStore, Subscriber {
    override fun load(stream: String): Result<Collection<EventStore.Event>> {
        // TODO: Can this leverage Kotlin coroutines?
        return client.readStream(stream)
            .thenApply { result -> result.events.map { it.toEventStoreEvent() } }
            .thenApply { Result.success(it) }
            .exceptionally { Result.failure(it) }
            .get()
            .also { it.map { events -> println("Load: $events") } }
    }

    override fun append(stream: String, events: Collection<EventStore.Event>): Result<Unit> {
        return client.appendToStream(stream, events.map { it.toEventData() }.iterator())
            .thenApply { Result.success(Unit) }
            .exceptionally { Result.failure(it) }
            .get()
            .also { it.map { println("Append: $events") } }
    }

    override fun subscribe(streamId: String, onEvent: (EventStore.Event) -> Result<Unit>) {
        client.subscribeToStream(streamId, object : SubscriptionListener() {
            override fun onError(subscription: Subscription, throwable: Throwable) {
                println("Error: ${subscription.subscriptionId} | $throwable")
            }

            override fun onEvent(subscription: Subscription, event: ResolvedEvent) {
                println("Subscription: Received [${event.event.eventType}] ${String(event.event.eventData)}")
                onEvent(event.toEventStoreEvent())
            }

            override fun onCancelled(subscription: Subscription) {
                println("Cancelled: ${subscription.subscriptionId}")
            }
        })
    }
}

