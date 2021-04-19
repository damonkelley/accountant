package com.damonkelley.accountant.infrastructure.eventstoredb

import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.tracing.EventTrace

interface EventStoreDBEventMapper<T> {
    fun toEvent(event: T, trace: EventTrace): Result<EventStore.Event>
    fun fromEvent(event: EventStore.Event): Result<T>
}