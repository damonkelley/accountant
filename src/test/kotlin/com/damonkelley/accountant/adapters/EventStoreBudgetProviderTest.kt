package com.damonkelley.accountant.adapters

import com.damonkelley.accountant.budget.domain.BudgetCreated
import com.damonkelley.accountant.budget.domain.BudgetEvent
import com.damonkelley.accountant.budget.domain.BudgetRenamed
import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.trace
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.allOf
import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.anyElement
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.hasElement
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.spekframework.spek2.Spek
import java.util.UUID

object EventStoreBudgetProviderTest : Spek({
    val name = "🍌 Banana Stand"
    group("#new") {
        group("when it succeeds") {
            test("the result is success") {
                val id = UUID.randomUUID()
                val eventStore = InMemoryEventStore()

                val result = EventStoreBudgetProvider(eventStore) { id }.new(trace()) { it.create(name) }

                assertThat(
                    result,
                    Matcher(Result<Unit>::isSuccess)
                )
            }
            test("it appends serialized events to the event store") {
                val id = UUID.randomUUID()
                val eventStore = InMemoryEventStore()

                EventStoreBudgetProvider(eventStore) { id }.new(trace()) { it.create(name) }

                assertThat(
                    eventStore.streams,
                    hasEventInStream("budget-$id", BudgetCreated(name))
                )
            }
        }

        group("when it fails") {
            test("the result is failure") {
                val result =
                    EventStoreBudgetProvider(EventStoreThatFails())
                        .new(trace()) { it.create(name) }

                assertThat(
                    result,
                    Matcher(Result<Unit>::isFailure)
                )
            }
        }
    }

    group("#load") {
        test("it loads deserialized events") {
            val id = UUID.randomUUID()
            val eventStore = InMemoryEventStore()

            val provider = EventStoreBudgetProvider(eventStore) { id }
            provider.new(trace()) { it.create("V1") }
            provider.load(id, trace()) { it?.create(name) }

            assertThat(
                eventStore.streams,
                hasEventInStream("budget-$id", BudgetCreated(name))
            )
        }

        group("when loading from the EventStore fails") {
            test("the result is failure") {
                val eventStore = object : EventStore {
                    override fun load(stream: String): Result<Collection<EventStore.Event>> =
                        Result.failure(Error("Unable to load"))

                    override fun append(stream: String, events: Collection<EventStore.Event>): Result<Unit> =
                        Result.success(Unit)
                }

                val result = EventStoreBudgetProvider(eventStore)
                    .load(UUID.randomUUID(), trace()) { it?.create(name) }

                assertThat(
                    result,
                    Matcher(Result<Unit>::isFailure)
                )
            }
        }

        group("when appending to the EventStore fails") {
            test("the result is failure") {
                val eventStore = object : EventStore {
                    override fun load(stream: String): Result<Collection<EventStore.Event>> =
                        Result.success(listOf(event()))

                    override fun append(stream: String, events: Collection<EventStore.Event>): Result<Unit> =
                        Result.failure(Error("Unable to append"))
                }

                val result = EventStoreBudgetProvider(eventStore)
                    .load(UUID.randomUUID(), trace()) { it?.create(name) }

                assertThat(
                    result,
                    Matcher(Result<Unit>::isFailure)
                )
            }
        }
    }
})

private fun event(): EventStore.Event {
    return EventStore.Event("BudgetCreated", Json.encodeToString(BudgetCreated("")))
}

fun hasEventInStream(streamId: String, event: BudgetEvent): Matcher<Map<String, Collection<EventStore.Event>>> {
    val expectedEvent = when (event) {
        is BudgetCreated -> EventStore.Event(eventType = "BudgetCreated", body = Json.encodeToString(event))
        is BudgetRenamed -> EventStore.Event(eventType = "BudgetRenamed", body = Json.encodeToString(event))
    }

    fun hasEventLike(event: EventStore.Event): Matcher<EventStore.Event> {
        return has(EventStore.Event::body, equalTo(event.body)) and
                has(EventStore.Event::eventType, equalTo(event.eventType))

    }

    return allOf(
        has("stream", { it.keys }, hasElement(streamId)),
        has("event", { it[streamId] ?: emptyList() }, anyElement(hasEventLike(expectedEvent))),
    )
}

class EventStoreThatFails : EventStore {
    override fun load(stream: String): Result<Collection<EventStore.Event>> {
        return Result.failure(Error("Unable to load"))
    }

    override fun append(stream: String, events: Collection<EventStore.Event>): Result<Unit> {
        return Result.failure(Error("Unable to append"))
    }

}

class InMemoryEventStore : EventStore {
    val streams = mutableMapOf<String, Collection<EventStore.Event>>()

    override fun load(stream: String): Result<Collection<EventStore.Event>> {
        return streams[stream]
            ?.let(Result.Companion::success)
            ?: Result.failure(Error("Stream not found"))
    }

    override fun append(stream: String, events: Collection<EventStore.Event>): Result<Unit> {
        streams.merge(stream, events) { previous, proposed -> previous.plus(proposed) }
        return Result.success(Unit)
    }
}