package com.damonkelley.accountant.budget.application

import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.eventsourcing.CanSave

interface SaveBudget : CanSave<Budget>