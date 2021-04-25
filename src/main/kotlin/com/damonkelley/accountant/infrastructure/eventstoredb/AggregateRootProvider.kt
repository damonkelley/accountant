package com.damonkelley.accountant.infrastructure.eventstoredb

import com.damonkelley.accountant.eventsourcing.CanLoad
import com.damonkelley.accountant.eventsourcing.CanSave
import com.damonkelley.accountant.eventsourcing.ReadableAggregateRoot
import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.common.result.extensions.combine
import com.damonkelley.common.result.extensions.flatMap
import java.util.UUID

class AggregateRootProvider<Event, ConcreteAggregateRoot>(
    private val eventStore: EventStore,
    private val category: String,
    private val construct: (UUID, List<Event>) -> ConcreteAggregateRoot,
    private val mapper: EventMapper<Event>
) : CanLoad<ConcreteAggregateRoot>,
    CanSave<ConcreteAggregateRoot>
where ConcreteAggregateRoot : ReadableAggregateRoot<Event> {
    override fun save(aggregateRoot: ConcreteAggregateRoot, trace: EventTrace): Result<ConcreteAggregateRoot> {
        return aggregateRoot.changes()
            .map { mapper.toEvent(it, trace) }
            .combine()
            .flatMap { eventStore.append("$category-${aggregateRoot.id}", it) }
            .map { aggregateRoot }
    }

    override fun load(id: UUID): Result<ConcreteAggregateRoot> {
        return eventStore.load("$category-$id")
            .flatMap { it.map(mapper::fromEvent).combine() }
            .map { facts -> construct(id, facts.toList())}
    }
}