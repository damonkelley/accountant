package com.damonkelley.accountant.eventstore

import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.common.result.extensions.flatMap

class EventStoreSubscription<Message>(
    private val eventStore: EventStoreSubscriber,
    private val serializer: EventDeserializer<Message>
) {
    fun of(stream: String, block: (EventTrace, Message) -> Result<Unit>) {
        eventStore.subscribe(stream) {
            serializer
                .deserialize(it.eventType, it.body)
                .flatMap { message -> block(it, message)}
        }
    }
}