package com.damonkelley.accountant.eventsourcing

import java.util.UUID

interface Repository<T> where T: AggregateRoot<*> {
    fun load(id: UUID): T?
    fun save(budget: T): Result<T>
}