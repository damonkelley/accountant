package com.damonkelley.accountant.infrastructure.eventstoredb

import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.eventsourcing.ExistingAggregateRootProvider
import com.damonkelley.accountant.eventsourcing.NewAggregateRootProvider
import com.damonkelley.accountant.eventsourcing.SimpleAggregateRoot
import com.damonkelley.accountant.eventsourcing.WritableAggregateRoot
import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.common.result.extensions.combine
import com.damonkelley.common.result.extensions.flatMap
import java.util.UUID

class EventStoreDBAggregateRootProvider<Event, ConcreteAggregateRoot>(
    private val eventStore: EventStore,
    private val category: String,
    private val construct: (WritableAggregateRoot<Event>) -> ConcreteAggregateRoot,
    private val mapper: EventStoreDBEventMapper<Event>,
    val UUIDProvider: () -> UUID = UUID::randomUUID
) : NewAggregateRootProvider<ConcreteAggregateRoot>, ExistingAggregateRootProvider<ConcreteAggregateRoot> {
    override fun new(trace: EventTrace, block: (ConcreteAggregateRoot) -> ConcreteAggregateRoot): Result<Unit> {
        val aggregateRoot = SimpleAggregateRoot<Event>(UUIDProvider(), emptyList())

        block(construct(aggregateRoot))

        return aggregateRoot.changes()
            .map { mapper.toEvent(it, trace) }
            .combine()
            .flatMap { eventStore.append("$category-${aggregateRoot.id}", it) }
    }

    override fun load(
        id: UUID,
        trace: EventTrace,
        block: (ConcreteAggregateRoot?) -> ConcreteAggregateRoot?
    ): Result<Unit> {
        return eventStore.load("$category-$id")
            .map { it.map(mapper::fromEvent) }
            .map { SimpleAggregateRoot<Event>(id, emptyList()) }
            .map { block(construct(it)).run { it.changes() } }
            .map { it.map { domainEvent -> mapper.toEvent(domainEvent, trace) } }
            .flatMap { it.combine() }
            .flatMap { eventStore.append("$category-$id", it) }
    }
}