package com.damonkelley.accountant.adapters

import com.damonkelley.accountant.budget.application.ExistingBudgetProvider
import com.damonkelley.accountant.budget.application.NewBudgetProvider
import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.BudgetCommand
import com.damonkelley.accountant.budget.domain.BudgetCreated
import com.damonkelley.accountant.budget.domain.BudgetEvent
import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.accountant.eventstore.Serializer
import com.damonkelley.accountant.infrastructure.eventstoredb.EventStoreAggregateRootProvider
import com.damonkelley.accountant.infrastructure.eventstoredb.EventStoreEventMapper
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class EventStoreBudgetProvider(
    private val provider: EventStoreAggregateRootProvider<BudgetEvent, Budget>
) :
    NewBudgetProvider,
    ExistingBudgetProvider {
    constructor(eventStore: EventStore, UUIDProvider: () -> UUID = UUID::randomUUID) : this(
        EventStoreAggregateRootProvider(
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

class BudgetEventMapper(private val serializer: Serializer<BudgetEvent>) : EventStoreEventMapper<BudgetEvent> {
    override fun toEvent(event: BudgetEvent, trace: EventTrace): Result<EventStore.Event> {
        return serializer.serialize(event)
            .map { EventStore.Event(eventType = event.eventType(), body = it, trace = trace) }
    }

    override fun fromEvent(event: EventStore.Event): Result<BudgetEvent> {
        return serializer.deserialize(event.eventType, event.body)
    }

    private fun BudgetEvent.eventType(): String = when (this) {
        is BudgetCreated -> "BudgetCreated"
    }
}

class BudgetEventSerializer : Serializer<BudgetEvent> {
    override fun deserialize(eventType: String, data: String): Result<BudgetEvent> {
        return when (eventType) {
            "BudgetCreated" -> try {
                Result.success(Json.decodeFromString<BudgetCreated>(data))
            } catch (e: Throwable) {
                Result.failure(e)
            }
            else -> Result.failure(Error("Unable to deserialize event with type $eventType"))
        }
    }

    override fun serialize(event: BudgetEvent): Result<String> {
        return when (event) {
            is BudgetCreated -> try {
                Result.success(Json.encodeToString(event))
            } catch (e: Throwable) {
                Result.failure(e)
            }
        }
    }
}

class BudgetCommandSerializer : Serializer<BudgetCommand> {
    override fun deserialize(eventType: String, data: String): Result<BudgetCommand> {
        return when (eventType) {
            "CreateBudget" -> try {
                Result.success(Json.decodeFromString<CreateBudget>(data))
            } catch (e: Throwable) {
                Result.failure(e)
            }
            else -> Result.failure(Error("Unable to deserialize event with type $eventType"))
        }
    }

    override fun serialize(event: BudgetCommand): Result<String> {
        return when (event) {
            is CreateBudget -> try {
                Result.success(Json.encodeToString(event))
            } catch (e: Throwable) {
                Result.failure(e)
            }
        }
    }
}
