package com.damonkelley.accountant.eventstore

import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.accountant.tracing.Trace
import java.util.UUID

interface EventStore {
    data class Event(
        val eventType: String,
        val body: String,
        private val trace: EventTrace = Trace(UUID.nameUUIDFromBytes(body.toByteArray())),
    ): EventTrace by trace

    fun load(stream: String): Result<Collection<Event>>
    fun append(stream: String, events: Collection<Event>): Result<Unit>
}