package com.damonkelley.accountant.eventstore

interface Subscriber {
    fun subscribe(streamId: String, onEvent: (EventStore.Event) -> Result<Unit>)
}