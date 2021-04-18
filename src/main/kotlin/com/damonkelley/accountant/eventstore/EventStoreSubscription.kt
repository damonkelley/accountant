package com.damonkelley.accountant.eventstore

class EventStoreSubscription<Command>(eventStore: EventStoreSubscriber) {
    fun of(stream: String, block: (Command) -> Result<Unit>) {
    }
}