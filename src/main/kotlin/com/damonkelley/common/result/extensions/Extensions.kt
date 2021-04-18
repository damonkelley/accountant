package com.damonkelley.common.result.extensions

fun <T,R> Result<T>.flatMap(function: (T) -> Result<R>): Result<R> {
    return fold(function) { Result.failure(it) }
}

fun <T> Collection<Result<T>>.combine(): Result<Collection<T>> {
    val initial = Result.success(emptyList<T>())

    return fold(initial) { acc: Result<Collection<T>>, result: Result<T> ->
        result.fold(
            { event: T -> acc.map { it.plus(event) } },
            { Result.failure(it) }
        )
    }
}
