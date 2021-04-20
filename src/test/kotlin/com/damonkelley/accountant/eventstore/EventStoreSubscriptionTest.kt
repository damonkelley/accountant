package com.damonkelley.accountant.eventstore

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.allElements
import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.isEmpty
import io.mockk.spyk
import io.mockk.verify
import kotlinx.serialization.Serializable
import org.spekframework.spek2.Spek
import java.util.UUID

object EventStoreSubscriptionTest : Spek({
    val serializer = TestMessageSerializer()

    group("#of") {
        test("it works") {
            val event = TestMessage("test")
            val subscriber = TestSubscriber()
                .append("stream-id", eventStoreEvent(serializer, event))

            Subscription(subscriber, serializer).of("stream-id") { _, _ ->
                Result.success(Unit)
            }

            assertThat(subscriber.outcomes, isEmpty.not() and allElements(Matcher(Result<Unit>::isSuccess)))
        }
    }

    test("it still works") {
        val testMessage = TestMessage()
        val subscriber = TestSubscriber()
            .append("stream-id", eventStoreEvent(serializer, testMessage))

        val onMessage = spyk({_: TestMessage -> Result.success(Unit)})

        Subscription(subscriber, serializer).of("stream-id") { _, command ->
            onMessage(command)
        }

        verify { onMessage(testMessage) }
    }
})

private fun eventStoreEvent(serializer: TestMessageSerializer, event: TestMessage): EventStore.Event {
    return EventStore.Event(
        eventType = "TestMessage",
        body = serializer.serialize(event).getOrThrow()
    )
}

@Serializable
data class TestMessage(val id: String = UUID.randomUUID().toString())

class TestSubscriber : Subscriber {
    private val streams = mutableMapOf<String, List<EventStore.Event>>()
    val outcomes = mutableListOf<Result<Unit>>()

    fun append(streamId: String, event: EventStore.Event): TestSubscriber {
        return apply {
            streams.merge(streamId, listOf(event)) { existing, new -> existing.plus(new) }
        }
    }

    override fun subscribe(streamId: String, onEvent: (EventStore.Event) -> Result<Unit>) {
        val results = streams[streamId]
            ?.map(onEvent)
            ?: throw Error("Stream does not exist")

        outcomes.addAll(results)
    }
}

class TestMessageSerializer: Serializer<TestMessage> {
    override fun deserialize(eventType: String, data: String): Result<TestMessage> {
        return Result.success(TestMessage(data))
    }

    override fun serialize(event: TestMessage): Result<String> {
        return Result.success(event.id)
    }
}
