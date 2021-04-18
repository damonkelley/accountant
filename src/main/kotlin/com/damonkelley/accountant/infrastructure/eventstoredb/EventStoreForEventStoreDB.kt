package com.damonkelley.accountant.infrastructure.eventstoredb

import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.eventstore.EventStoreSubscriber
import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.ResolvedEvent
import java.util.UUID

class EventStoreForEventStoreDB(private val client: EventStoreDBClient) : EventStore, EventStoreSubscriber {
    override fun load(stream: String): Result<Collection<EventStore.Event>> {
        // TODO: Can this leverage Kotlin coroutines?
        return client.readStream(stream)
            .thenApply { result -> result.events.map { it.toEventStoreEvent() } }
            .thenApply { Result.success(it) }
            .exceptionally { Result.failure(it) }
            .get()
    }

    override fun append(stream: String, events: Collection<EventStore.Event>): Result<Unit> {
        return client.appendToStream(stream, events.map { it.toEventData() }.iterator())
            .thenApply { Result.success(Unit) }
            .exceptionally { Result.failure(it) }
            .get()
    }

    private fun ResolvedEvent.toEventStoreEvent(): EventStore.Event {
        return EventStore.Event(eventType = event.eventType, body = String(event.eventData))
    }

    private fun EventStore.Event.toEventData(): EventData {
        return EventData(
            UUID.randomUUID(),
            eventType,
            "application/json",
            body.toByteArray(),
            "".toByteArray()
        )
    }
}