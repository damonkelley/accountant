package com.damonkelley.accountant.eventsourcing

import java.util.UUID

data class Context(
        val id: UUID,
        val streamId: String,
        val correlationId: UUID,
        val causationId: UUID,
) {
    constructor(id: UUID, streamId: String): this(id,streamId, id,id)
}