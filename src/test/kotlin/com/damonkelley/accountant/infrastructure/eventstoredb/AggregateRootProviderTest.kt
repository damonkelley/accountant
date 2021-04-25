package com.damonkelley.accountant.infrastructure.eventstoredb

import com.damonkelley.accountant.eventsourcing.AggregateRoot
import com.damonkelley.accountant.eventsourcing.ReadableAggregateRoot
import com.damonkelley.accountant.eventsourcing.SimpleAggregateRoot
import com.damonkelley.accountant.eventstore.EventStore
import com.damonkelley.accountant.trace
import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.common.result.extensions.combine
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.allOf
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.hasElement
import io.kotest.core.spec.style.FunSpec
import java.util.UUID

class AggregateRootProviderTest : FunSpec({
    val trace = trace()
    context("save") {
        test("it saves an aggregate root").config(enabled = false) {
            val mapper = TestEventMapper()
            val eventStore = InMemoryEventStore()

            val provider = AggregateRootProvider(
                eventStore = eventStore,
                category = "category",
                construct = ::DomainObject,
                mapper = mapper
            )

            val domainObject =
                DomainObject(SimpleAggregateRoot(id = UUID.randomUUID(), facts = emptyList()))
                    .apply { perform() }

            provider.save(domainObject, trace)

            val expectedEventToBePublished = domainObject.changes()
                .toList()
                .map { mapper.toEvent(it, trace) }
                .combine()
                .getOrThrow()

            assertThat(eventStore, allOf(
                expectedEventToBePublished.map { published("category-${domainObject.id}", it) }
            ))
        }
    }
})

fun published(to: String, event: EventStore.Event): Matcher<InMemoryEventStore> {
    return has("published", { it.streams[to]!! }, hasElement(event))
}

data class TestEvent(val id: String)

class DomainObject(private val aggregateRoot: AggregateRoot<TestEvent>) :
    ReadableAggregateRoot<TestEvent> by aggregateRoot {
    fun perform() {
        aggregateRoot.raise(TestEvent(id = "AAA"))
    }
}

class TestEventMapper : EventMapper<TestEvent> {
    override fun toEvent(event: TestEvent, trace: EventTrace): Result<EventStore.Event> {
        return EventStore.Event(
            eventType = "TestEvent.A",
            body = event.id,
            trace
        ).let { Result.success(it) }
    }

    override fun fromEvent(event: EventStore.Event): Result<TestEvent> {
        return Result.success(TestEvent(id = event.body))
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
