package com.damonkelley.accountant.eventsourcing

import java.util.UUID

interface AggregateRoot<T> {
    val id: UUID
    val changes: List<T>
}
