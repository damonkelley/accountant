package com.damonkelley.accountant.eventsourcing

interface AggregateRoot<T> {
    val recording: Recordable<T>
}
