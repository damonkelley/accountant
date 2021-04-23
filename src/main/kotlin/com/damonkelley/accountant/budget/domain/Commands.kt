package com.damonkelley.accountant.budget.domain

import com.damonkelley.domainevents.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

sealed class BudgetCommand

@Serializable
data class CreateBudget(
        @SerialName("name")
        val name: String
) : BudgetCommand()

@Serializable
data class RenameBudget(
        @Serializable(with = UUIDSerializer::class)
        val budgetId: UUID,

        @SerialName("name")
        val name: String
) : BudgetCommand()