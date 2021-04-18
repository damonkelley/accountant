package com.damonkelley.accountant.infrastructure.eventstoredb

interface EventSerializer<T> {
    fun serialize(event: T): Result<String>
    fun deserialize(eventType: String, data: String): Result<T>
}