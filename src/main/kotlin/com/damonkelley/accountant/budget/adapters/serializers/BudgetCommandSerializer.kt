package com.damonkelley.accountant.budget.adapters.serializers

import com.damonkelley.accountant.budget.domain.BudgetCommand
import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.budget.domain.RenameBudget
import com.damonkelley.accountant.eventstore.Serializer

class BudgetCommandSerializer : Serializer<BudgetCommand> {
    override fun deserialize(eventType: String, data: String): Result<BudgetCommand> {
        return when (eventType) {
            "CreateBudget" -> data.fromJson<CreateBudget>()
            "RenameBudget" -> data.fromJson<RenameBudget>()
            else -> Result.failure(Error("Unable to deserialize event with type $eventType"))
        }
    }

    override fun serialize(event: BudgetCommand): Result<String> {
        return when (event) {
            is CreateBudget -> event.toJson()
            is RenameBudget -> event.toJson()
        }
    }
}