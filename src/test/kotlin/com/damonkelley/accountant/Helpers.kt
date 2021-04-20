package com.damonkelley.accountant

import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.accountant.tracing.Trace
import java.util.UUID

fun trace(): EventTrace {
    return Trace(id = UUID.randomUUID())
}