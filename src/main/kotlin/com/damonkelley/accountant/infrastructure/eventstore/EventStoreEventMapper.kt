package com.damonkelley.accountant.infrastructure.eventstore

import com.damonkelley.accountant.eventsourcing.EventStore

interface EventStoreEventMapper<T> {
    fun toEvent(event: T): Result<EventStore.Event>
    fun fromEvent(event: EventStore.Event): Result<T>
}