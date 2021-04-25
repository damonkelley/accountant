package com.damonkelley.accountant.budget.adapters

import com.damonkelley.accountant.budget.domain.BudgetCreated
import com.damonkelley.accountant.budget.domain.BudgetEvent
import com.damonkelley.accountant.budget.domain.BudgetRenamed
import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.eventstore.Serializer
import com.damonkelley.accountant.infrastructure.eventstoredb.EventMapper
import com.damonkelley.accountant.tracing.EventTrace

class BudgetEventMapper(private val serializer: Serializer<BudgetEvent>) : EventMapper<BudgetEvent> {
    override fun toEvent(event: BudgetEvent, trace: EventTrace): Result<EventStore.Event> {
        return serializer.serialize(event)
            .map { EventStore.Event(eventType = event.eventType(), body = it, trace = trace) }
    }

    override fun fromEvent(event: EventStore.Event): Result<BudgetEvent> {
        return serializer.deserialize(event.eventType, event.body)
    }

    private fun BudgetEvent.eventType(): String = when (this) {
        is BudgetCreated -> "BudgetCreated"
        is BudgetRenamed -> "BudgetRenamed"
    }
}