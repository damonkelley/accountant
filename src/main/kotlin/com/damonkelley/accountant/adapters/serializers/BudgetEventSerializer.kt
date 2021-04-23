package com.damonkelley.accountant.adapters.serializers

import com.damonkelley.accountant.budget.domain.BudgetCreated
import com.damonkelley.accountant.budget.domain.BudgetEvent
import com.damonkelley.accountant.budget.domain.BudgetRenamed
import com.damonkelley.accountant.eventstore.Serializer

class BudgetEventSerializer : Serializer<BudgetEvent> {
    override fun deserialize(eventType: String, data: String): Result<BudgetEvent> {
        return when (eventType) {
            "BudgetCreated" -> data.fromJson<BudgetCreated>()
            "BudgetRenamed" -> data.fromJson<BudgetRenamed>()
            else -> Result.failure(Error("Unable to deserialize event with type $eventType"))
        }
    }

    override fun serialize(event: BudgetEvent): Result<String> {
        return when (event) {
            is BudgetCreated -> event.toJson()
            is BudgetRenamed -> event.toJson()
        }
    }
}