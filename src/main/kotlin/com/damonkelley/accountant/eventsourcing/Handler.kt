package com.damonkelley.accountant.eventsourcing

import com.damonkelley.accountant.tracing.EventTrace

interface Handler<T> {
    fun handle(trace: EventTrace, command: T): Result<Unit>
}

