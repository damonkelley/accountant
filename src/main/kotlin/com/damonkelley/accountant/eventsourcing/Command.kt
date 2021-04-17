package com.damonkelley.accountant.eventsourcing

data class Command<T>(val context: Context, val function: (Context) -> T) {
    val body = function(context)
}