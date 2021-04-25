package com.damonkelley.accountant.budget.adapters

import com.damonkelley.accountant.budget.adapters.serializers.BudgetEventSerializer
import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.BudgetEvent
import com.damonkelley.accountant.eventsourcing.ExistingAggregateRootProvider
import com.damonkelley.accountant.eventsourcing.NewAggregateRootProvider
import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.infrastructure.eventstoredb.AggregateRootProvider
import java.util.UUID

class EventStoreBudgetProvider(
    private val provider: AggregateRootProvider<BudgetEvent, Budget>
) :
    NewAggregateRootProvider<Budget> by provider,
    ExistingAggregateRootProvider<Budget> by provider {
    constructor(eventStore: EventStore, UUIDProvider: () -> UUID = UUID::randomUUID) : this(
        AggregateRootProvider(
            eventStore = eventStore,
            category = "budget",
            construct = ::Budget,
            mapper = BudgetEventMapper(BudgetEventSerializer()),
            UUIDProvider = UUIDProvider,
        )
    )
}