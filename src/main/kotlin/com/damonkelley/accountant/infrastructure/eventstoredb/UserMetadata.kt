package com.damonkelley.accountant.infrastructure.eventstoredb

import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.domainevents.serialization.UUIDSerializer
import com.eventstore.dbclient.ResolvedEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.UUID

@Serializable
data class UserMetadata(
    @SerialName("\$correlationId")
    @Serializable(with = UUIDSerializer::class)
    val correlationId: UUID,

    @SerialName("\$causationId")
    @Serializable(with = UUIDSerializer::class)
    val causationId: UUID
) {
    companion object {
        fun from(trace: EventTrace): UserMetadata {
            return UserMetadata(
                correlationId = trace.correlationId,
                causationId = trace.causationId
            )
        }

        fun from(event: ResolvedEvent): UserMetadata? {
            return try {
                Json.decodeFromString<UserMetadata>(String(event.event.userMetadata))
            } catch (e: SerializationException) {
                null
            }
        }
    }
}