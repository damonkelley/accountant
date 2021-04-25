package com.damonkelley.accountant.budget.adapters.serializers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> T.toJson(): Result<String> {
    return try {
        Result.success(Json.encodeToString(this))
    } catch (e: Throwable) {
        Result.failure(e)
    }
}

inline fun <reified T> String.fromJson() : Result<T> = try {
    Result.success(Json.decodeFromString(this))
} catch (e: Throwable) {
    Result.failure(e)
}