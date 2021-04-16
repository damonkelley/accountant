package com.damonkelley.accountant.infrastructure

import com.damonkelley.accountant.eventsourcing.AggregateRoot
import java.util.UUID

data class Context(
        val id: UUID,
        val streamId: String,
        val correlationId: UUID,
        val causationId: UUID,
) {
    constructor(id: UUID, streamId: String): this(id,streamId, id,id)
}

fun <T> AggregateRoot<T>.changes(via: Context): List<Event<T>> {
    return changes.map { event -> Event(via) { event } }
}

data class Event<R>(val context: Context, val function: (Context) -> R) {
    val body: R = function(context)
}

data class Command<T>(val context: Context, val function: (Context) -> T) {
    val body = function(context)
}