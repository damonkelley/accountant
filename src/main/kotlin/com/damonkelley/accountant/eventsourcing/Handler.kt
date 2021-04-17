package com.damonkelley.accountant.eventsourcing

interface Handler<T> {
    fun handle(command: T): Result<Unit>
}

