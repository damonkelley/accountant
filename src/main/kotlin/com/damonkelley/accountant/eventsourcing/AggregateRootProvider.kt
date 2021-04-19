package com.damonkelley.accountant.eventsourcing

import com.damonkelley.accountant.tracing.EventTrace
import java.util.UUID

interface NewAggregateRootProvider<ConcreteAggregateRoot> {
    fun new(trace: EventTrace, block: (ConcreteAggregateRoot) -> ConcreteAggregateRoot): Result<Unit>
}

interface ExistingAggregateRootProvider<ConcreteAggregateRoot> {
    fun load(id: UUID, trace: EventTrace, block: (ConcreteAggregateRoot?) -> ConcreteAggregateRoot?): Result<Unit>
}