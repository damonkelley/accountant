package com.damonkelley.accountant.eventstore

interface EventStoreSubscriber {
    fun subscribe(streamId: String, onEvent: (EventStore.Event) -> Result<Unit>)
}