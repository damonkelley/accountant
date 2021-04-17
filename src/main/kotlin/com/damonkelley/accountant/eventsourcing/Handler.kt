package com.damonkelley.accountant.eventsourcing

interface Handler<T> {
    fun handle(command: Command<T>): Result<Unit>
}

