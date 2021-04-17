package com.damonkelley.accountant.eventsourcing

// TODO: Should this be used for Write side and Read side? Or should repository have a different interface?
interface Recordable<T> {
    fun record(event: T): Recordable<T>
    fun replayFacts(apply: (T) -> Unit): Recordable<T>
    fun replayChanges(apply: (T)-> Unit): Recordable<T>
}

interface RecordableProvider<T> {
    fun from(context: Context): Recordable<T>
}