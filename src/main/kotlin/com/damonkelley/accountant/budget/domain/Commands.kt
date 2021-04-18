package com.damonkelley.accountant.budget.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class BudgetCommand

@Serializable
data class CreateBudget(
        @SerialName("name")
        val name: String
) : BudgetCommand()