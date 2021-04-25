package com.damonkelley.accountant.eventsourcing

import com.damonkelley.accountant.tracing.EventTrace
import java.util.UUID

interface CanLoad<ConcreteAggregateRoot> {
    fun load(id: UUID): Result<ConcreteAggregateRoot>
}

interface CanSave<ConcreteAggregateRoot> {
    fun save(aggregateRoot: ConcreteAggregateRoot, trace: EventTrace): Result<ConcreteAggregateRoot>
}