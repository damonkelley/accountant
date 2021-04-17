package com.damonkelley.accountant.adapters

import com.damonkelley.accountant.budget.application.ExistingBudgetProvider
import com.damonkelley.accountant.budget.application.NewBudgetProvider
import com.damonkelley.accountant.budget.domain.Budget
import java.util.UUID

class EventStoreBudgetRepository(): NewBudgetProvider, ExistingBudgetProvider {
    override fun new(block: (Budget) -> Budget): Result<Budget> {
        TODO("Not yet implemented")
    }

    override fun load(id: UUID, block: (Budget?) -> Budget?): Result<Budget?> {
        TODO("Not yet implemented")
    }
}