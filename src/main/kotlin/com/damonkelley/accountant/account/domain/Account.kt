package com.damonkelley.accountant.account.domain

import com.damonkelley.accountant.eventsourcing.AggregateRoot
import com.damonkelley.accountant.eventsourcing.SimpleAggregateRoot
import java.util.UUID

class Account(aggregateRoot: AggregateRoot<Unit> = SimpleAggregateRoot(id = UUID.randomUUID())) :
    AggregateRoot<Unit> by aggregateRoot {
    lateinit var name: String
    lateinit var budgetId: UUID

    fun create(name: String, budgetId: UUID): Account {
        this.name = name
        this.budgetId = budgetId
        return this
    }
}