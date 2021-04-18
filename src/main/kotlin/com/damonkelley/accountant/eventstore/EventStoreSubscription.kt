package com.damonkelley.accountant.eventstore

import com.damonkelley.common.result.extensions.flatMap

class EventStoreSubscription<Message>(
    private val eventStore: EventStoreSubscriber,
    private val serializer: EventDeserializer<Message>
) {
    fun of(stream: String, block: (Message) -> Result<Unit>) {
        eventStore.subscribe(stream) {
            serializer
                .deserialize(it.eventType, it.body)
                .flatMap(block)
        }
    }
}