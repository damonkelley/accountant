package com.damonkelley.accountant.adapters

import com.damonkelley.accountant.adapters.serializers.BudgetEventSerializer
import com.damonkelley.accountant.budget.application.ExistingBudgetProvider
import com.damonkelley.accountant.budget.application.NewBudgetProvider
import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.BudgetEvent
import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.accountant.infrastructure.eventstoredb.AggregateRootProvider
import java.util.UUID

class EventStoreBudgetProvider(
    private val provider: AggregateRootProvider<BudgetEvent, Budget>
) :
    NewBudgetProvider,
    ExistingBudgetProvider {
    constructor(eventStore: EventStore, UUIDProvider: () -> UUID = UUID::randomUUID) : this(
        AggregateRootProvider(
            eventStore = eventStore,
            category = "budget",
            construct = ::Budget,
            mapper = BudgetEventMapper(BudgetEventSerializer()),
            UUIDProvider = UUIDProvider,
        )
    )

    override fun new(trace: EventTrace, block: (Budget) -> Budget): Result<Unit> {
        return provider.new(trace, block)
    }

    override fun load(id: UUID, trace: EventTrace, block: (Budget?) -> Budget?): Result<Unit> {
        return provider.load(id, trace, block)
    }
}