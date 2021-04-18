package com.damonkelley.accountant.budget.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class BudgetEvent

@Serializable
data class BudgetCreated(
        @SerialName("name")
        val name: String
) : BudgetEvent()

