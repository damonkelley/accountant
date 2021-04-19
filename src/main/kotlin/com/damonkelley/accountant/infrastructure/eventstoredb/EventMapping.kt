package com.damonkelley.accountant.infrastructure.eventstoredb

import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.tracing.Trace
import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.ResolvedEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

internal fun ResolvedEvent.toEventStoreEvent(): EventStore.Event {
    val userMetadata = UserMetadata.from(this)
    return EventStore.Event(
        eventType = event.eventType,
        body = String(event.eventData),
        trace = Trace(
            id = event.eventId,
            correlationId = userMetadata?.correlationId ?: event.eventId,
            causationId = userMetadata?.causationId ?: event.eventId
        )
    )
}

internal fun EventStore.Event.toEventData(): EventData {
    return EventData(
        UUID.randomUUID(),
        eventType,
        "application/json",
        body.toByteArray(),
        Json.encodeToString(UserMetadata.from(this)).toByteArray()
    )
}
