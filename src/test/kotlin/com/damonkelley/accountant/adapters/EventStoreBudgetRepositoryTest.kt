package com.damonkelley.accountant.adapters

import com.damonkelley.accountant.budget.domain.BudgetCreated
import com.damonkelley.accountant.budget.domain.BudgetEvent
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.allOf
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.hasElement
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.spekframework.spek2.Spek
import java.util.UUID

object EventStoreBudgetRepositoryTest : Spek({
    val name = "🍌 Banana Stand"
    group("#new") {
        group("when it succeeds") {
            test("the result is success") {
                val id = UUID.randomUUID()
                val eventStore = InMemoryEventStore()

                val result = EventStoreBudgetRepository(eventStore) { id }.new { it.create(name) }

                assertThat(
                    result,
                    Matcher(Result<Unit>::isSuccess)
                )
            }
            test("it appends serialized events to the event store") {
                val id = UUID.randomUUID()
                val eventStore = InMemoryEventStore()

                EventStoreBudgetRepository(eventStore) { id }.new { it.create(name) }

                assertThat(
                    eventStore.streams,
                    hasEventInStream("budget-$id", BudgetCreated(name))
                )
            }
        }

        group("when it fails") {
            test("the result is failure") {
                val result = EventStoreBudgetRepository(EventStoreThatFails()).new { it.create(name) }

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

            val provider = EventStoreBudgetRepository(eventStore) { id }
            provider.new { it.create("V1") }
            provider.load(id) { it?.create(name) }

            assertThat(
                eventStore.streams,
                hasEventInStream("budget-$id", BudgetCreated(name))
            )
        }
    }
})

fun hasEventInStream(streamId: String, event: BudgetEvent): Matcher<Map<String, Collection<EventStore.Event>>> {
    val expectedEvent = when (event) {
        is BudgetCreated -> {
            EventStore.Event(
                eventType = "BudgetCreated",
                body = Json.encodeToString(event)
            )
        }
    }

    return allOf(
        has("stream", { it.keys }, hasElement(streamId)),
        has("event", { it[streamId] ?: emptyList() }, hasElement(expectedEvent)),
    )
}

class EventStoreThatFails: EventStore {
    override fun load(stream: String): Result<Collection<EventStore.Event>> {
        TODO("Not yet implemented")
    }

    override fun append(stream: String, events: Collection<EventStore.Event>): Result<Unit> {
        return Result.failure(Error("Unable to append"))
    }

}

class InMemoryEventStore : EventStore {
    val streams = mutableMapOf<String, Collection<EventStore.Event>>()

    override fun load(stream: String): Result<Collection<EventStore.Event>> {
        // TODO: how should we handle a stream that doesn't exist
        return Result.success(streams[stream] ?: emptyList())
    }

    override fun append(stream: String, events: Collection<EventStore.Event>): Result<Unit> {
        streams.merge(stream, events) { previous, proposed -> previous.plus(proposed) }
        return Result.success(Unit)
    }
}
