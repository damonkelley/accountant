package com.damonkelley.accountant.eventsourcing

import java.util.UUID

interface AggregateRootProvider<ConcreteAggregateRoot> {
    fun new(block: (ConcreteAggregateRoot) -> ConcreteAggregateRoot): Result<Unit>
    fun load(id: UUID, block: (ConcreteAggregateRoot?) -> ConcreteAggregateRoot?): Result<Unit>
}