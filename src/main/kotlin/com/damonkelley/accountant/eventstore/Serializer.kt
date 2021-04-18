package com.damonkelley.accountant.eventstore

interface Serializer<T>: EventSerializer<T>, EventDeserializer<T>

interface EventSerializer<T> {
    fun serialize(event: T): Result<String>
}

interface EventDeserializer<T> {
    fun deserialize(eventType: String, data: String): Result<T>
}