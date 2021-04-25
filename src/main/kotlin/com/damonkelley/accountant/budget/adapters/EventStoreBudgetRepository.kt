package com.damonkelley.accountant.budget.adapters

import com.damonkelley.accountant.budget.adapters.serializers.BudgetEventSerializer
import com.damonkelley.accountant.budget.application.LoadBudget
import com.damonkelley.accountant.budget.application.SaveBudget
import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.BudgetEvent
import com.damonkelley.accountant.eventsourcing.CanLoad
import com.damonkelley.accountant.eventsourcing.CanSave
import com.damonkelley.accountant.eventsourcing.SimpleAggregateRoot
import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.infrastructure.eventstoredb.AggregateRootProvider

class EventStoreBudgetRepository(private val provider: AggregateRootProvider<BudgetEvent, Budget>) :
    SaveBudget,
    LoadBudget,
    CanSave<Budget> by provider,
    CanLoad<Budget> by provider {
        constructor(eventStore: EventStore): this(
            AggregateRootProvider(
                eventStore = eventStore,
                category = "budget",
                construct = { id, facts -> Budget(SimpleAggregateRoot(id, facts))},
                mapper = BudgetEventMapper(BudgetEventSerializer())
            )
        )
    }