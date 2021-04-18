package com.damonkelley.accountant.infrastructure.eventstore

import com.damonkelley.accountant.eventsourcing.EventStore
import com.damonkelley.accountant.eventsourcing.AggregateRootProvider
import com.damonkelley.accountant.eventsourcing.SimpleAggregateRoot
import com.damonkelley.accountant.eventsourcing.WritableAggregateRoot
import com.damonkelley.common.result.extensions.combine
import com.damonkelley.common.result.extensions.flatMap
import java.util.UUID

class EventStoreAggregateRootProvider<Event, ConcreteAggregateRoot>(
    private val eventStore: EventStore,
    private val category: String,
    private val construct: (WritableAggregateRoot<Event>) -> ConcreteAggregateRoot,
    private val mapper: EventStoreEventMapper<Event>,
    val UUIDProvider: () -> UUID = UUID::randomUUID
) : AggregateRootProvider<ConcreteAggregateRoot> {
    override fun new(block: (ConcreteAggregateRoot) -> ConcreteAggregateRoot): Result<Unit> {
        val aggregateRoot = SimpleAggregateRoot<Event>(UUIDProvider(), emptyList())

        block(construct(aggregateRoot))

        return aggregateRoot.changes()
            .map { mapper.toEvent(it) }
            .combine()
            .flatMap { eventStore.append("$category-${aggregateRoot.id}", it) }
    }

    override fun load(id: UUID, block: (ConcreteAggregateRoot?) -> ConcreteAggregateRoot?): Result<Unit> {
        return eventStore.load("$category-$id")
            .map { it.map(mapper::fromEvent) }
            .map { SimpleAggregateRoot<Event>(id, emptyList()) }
            .map { block(construct(it)).run { it.changes() } }
            .map { it.map(mapper::toEvent) }
            .flatMap { it.combine() }
            .flatMap { eventStore.append("$category-$id", it) }
    }
}