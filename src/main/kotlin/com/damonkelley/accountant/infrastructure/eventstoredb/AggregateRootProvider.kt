package com.damonkelley.accountant.infrastructure.eventstoredb

import com.damonkelley.accountant.eventsourcing.AggregateRoot
import com.damonkelley.accountant.eventsourcing.CanLoad
import com.damonkelley.accountant.eventsourcing.CanSave
import com.damonkelley.accountant.eventsourcing.SimpleAggregateRoot
import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.common.result.extensions.combine
import com.damonkelley.common.result.extensions.flatMap
import java.util.UUID

class AggregateRootProvider<Event, ConcreteAggregateRoot>(
    private val eventStore: EventStore,
    private val category: String,
    private val construct: (AggregateRoot<Event>) -> ConcreteAggregateRoot,
    private val mapper: EventMapper<Event>,
    val UUIDProvider: () -> UUID = UUID::randomUUID
) : CanLoad<ConcreteAggregateRoot>,
    CanSave<ConcreteAggregateRoot> {
    fun new(trace: EventTrace, block: (ConcreteAggregateRoot) -> ConcreteAggregateRoot): Result<Unit> {
        val aggregateRoot = SimpleAggregateRoot<Event>(UUIDProvider(), emptyList())

        block(construct(aggregateRoot))

        return aggregateRoot.changes()
            .map { mapper.toEvent(it, trace) }
            .combine()
            .flatMap { eventStore.append("$category-${aggregateRoot.id}", it) }
    }

    fun load(
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

    override fun save(aggregateRoot: ConcreteAggregateRoot, trace: EventTrace): Result<ConcreteAggregateRoot> {
        TODO("Not yet implemented")
    }

    override fun load(id: UUID): Result<ConcreteAggregateRoot> {
        TODO("Not yet implemented")
    }
}