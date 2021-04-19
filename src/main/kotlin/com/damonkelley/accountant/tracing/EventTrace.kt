package com.damonkelley.accountant.tracing

import java.util.UUID

interface EventTrace {
    val id: UUID
    val correlationId: UUID
    val causationId: UUID
}

data class Trace(
    override val id: UUID,
    override val correlationId: UUID,
    override val causationId: UUID
): EventTrace {
    constructor(id: UUID): this(id, id, id)
}