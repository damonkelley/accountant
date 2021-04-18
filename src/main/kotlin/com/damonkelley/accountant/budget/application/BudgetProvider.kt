package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.Budget
import java.util.UUID

interface NewBudgetProvider {
    fun new(block: (Budget) -> Budget): Result<Unit>
}

interface ExistingBudgetProvider {
    fun load(id: UUID, block: (Budget?) -> Budget?): Result<Unit>
}