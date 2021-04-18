package com.damonkelley.accountant.eventstore

interface EventStore {
    data class Event(
        val eventType: String,
        val body: String,
    )

    fun load(stream: String): Result<Collection<Event>>
    fun append(stream: String, events: Collection<Event>): Result<Unit>
}