package com.damonkelley.accountant.eventsourcing

interface Handler<T, R> {
    fun handle(command: T): AggregateRoot<R>
}