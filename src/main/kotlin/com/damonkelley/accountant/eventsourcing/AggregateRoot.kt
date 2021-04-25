package com.damonkelley.accountant.eventsourcing

import java.util.UUID

// TODO: Should this be used for Write side and Read side? Or should repository have a different interface?
interface WritableAggregateRoot<T> {
    val id: UUID
    fun raise(event: T): WritableAggregateRoot<T>
    fun facts(consumer: (T) -> Unit): WritableAggregateRoot<T>
}

interface ReadableAggregateRoot<T> {
    val id: UUID
    fun changes(): Collection<T>
}

interface AggregateRoot<T>: ReadableAggregateRoot<T>, WritableAggregateRoot<T>

class SimpleAggregateRoot<T>(override val id: UUID, val facts: Collection<T> = emptyList()): WritableAggregateRoot<T>, ReadableAggregateRoot<T>, AggregateRoot<T> {
    private val changes = mutableListOf<T>()

    override fun raise(event: T): WritableAggregateRoot<T> {
        return apply { changes.add(event) }
    }

    override fun facts(consumer: (T) -> Unit): WritableAggregateRoot<T> {
        return apply { facts.forEach(consumer) }
    }

    override fun changes(): Collection<T> {
        return changes
    }
}