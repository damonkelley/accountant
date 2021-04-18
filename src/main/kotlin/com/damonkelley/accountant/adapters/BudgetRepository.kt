package com.damonkelley.accountant.adapters

import com.damonkelley.accountant.budget.application.ExistingBudgetProvider
import com.damonkelley.accountant.budget.application.NewBudgetProvider
import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.BudgetCreated
import com.damonkelley.accountant.budget.domain.BudgetEvent
import com.damonkelley.accountant.eventsourcing.SimpleAggregateRoot
import com.damonkelley.common.result.extensions.combine
import com.damonkelley.common.result.extensions.flatMap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID


interface EventStore {
    data class Event(
        val eventType: String,
        val body: String,
    )

    fun load(stream: String): Result<Collection<Event>>
    fun append(stream: String, events: Collection<Event>): Result<Unit>
}

class EventStoreBudgetRepository(private val eventStore: EventStore, val UUIDProvider: () -> UUID = UUID::randomUUID) :
    NewBudgetProvider,
    ExistingBudgetProvider {
    override fun new(block: (Budget) -> Budget): Result<Unit> {
        val aggregateRoot = SimpleAggregateRoot<BudgetEvent>(UUIDProvider(), emptyList())

        block(Budget(aggregateRoot))

        return aggregateRoot.changes()
            .map { event -> event.asEventStoreEvent() }
            .combine()
            .flatMap { eventStore.append("budget-${aggregateRoot.id}", it) }
    }

    override fun load(id: UUID, block: (Budget?) -> Budget?): Result<Unit> {
        return eventStore.load("budget-$id")
            .map { it.map { event -> BudgetEventSerializer().deserialize(event.eventType, event.body).getOrThrow() } }
            .map { SimpleAggregateRoot<BudgetEvent>(id, emptyList()) }
            .map { block(Budget(it)).run { it.changes() } }
            .map { it.map { event -> event.asEventStoreEvent().getOrThrow() } }
            .map { eventStore.append("budget-$id", it) }
            .map { Result.success(Unit) }
    }

    private fun BudgetEvent.eventType(): String = when (this) {
        is BudgetCreated -> "BudgetCreated"
    }

    private fun BudgetEvent.asEventStoreEvent(): Result<EventStore.Event> {
        return BudgetEventSerializer()
            .serialize(this)
            .map { EventStore.Event(eventType = eventType(), body = it) }
    }


    class BudgetEventSerializer {
        fun deserialize(eventType: String, data: String): Result<BudgetEvent> {
            return when (eventType) {
                "BudgetCreated" -> try {
                    Result.success(Json.decodeFromString<BudgetCreated>(data))
                } catch (e: Throwable) {
                    Result.failure(e)
                }
                else -> Result.failure(Error("Unable to deserialize event with type $eventType"))
            }
        }

        fun serialize(event: BudgetEvent): Result<String> {
            return when (event) {
                is BudgetCreated -> try {
                    Result.success(Json.encodeToString(event))
                } catch (e: Throwable) {
                    Result.failure(e)
                }
            }
        }
    }
}