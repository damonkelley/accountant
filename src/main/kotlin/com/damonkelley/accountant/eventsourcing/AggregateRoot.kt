package com.damonkelley.accountant.eventsourcing

// TODO: Should this be used for Write side and Read side? Or should repository have a different interface?
interface AggregateRoot<T> {
    fun record(event: T): AggregateRoot<T>
    fun replayFacts(apply: (T) -> Unit): AggregateRoot<T>
    fun replayChanges(apply: (T)-> Unit): AggregateRoot<T>
}