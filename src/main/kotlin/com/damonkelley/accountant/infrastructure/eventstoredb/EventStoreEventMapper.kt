package com.damonkelley.accountant.infrastructure.eventstoredb

import com.damonkelley.accountant.eventstore.EventStore

interface EventStoreEventMapper<T> {
    fun toEvent(event: T): Result<EventStore.Event>
    fun fromEvent(event: EventStore.Event): Result<T>
}