package com.damonkelley.accountant.budget.domain

import com.damonkelley.accountant.account.domain.Account
import com.damonkelley.accountant.eventsourcing.AggregateRoot
import com.damonkelley.accountant.eventsourcing.SimpleAggregateRoot
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.hasElement
import io.kotest.core.spec.style.FunSpec
import java.util.UUID

class BudgetTest : FunSpec({
    val id = UUID.nameUUIDFromBytes(this::class.java.simpleName.toByteArray())
    context("create") {
        test("creates a new budget") {
            val aggregateRoot = SimpleAggregateRoot<BudgetEvent>(id)
            Budget(aggregateRoot).create("My first budget")

            assertThat(aggregateRoot, published(BudgetCreated("My first budget")))
        }
    }

    context("rename") {
        test("renames the budget") {
            val aggregateRoot = SimpleAggregateRoot<BudgetEvent>(id = id)

            Budget(aggregateRoot)
                .create("My first budget")
                .rename("My budget")

            assertThat(aggregateRoot, published(BudgetRenamed("My budget")))
        }

        test("it sources the rename event") {
            val budget = Budget(SimpleAggregateRoot(id = id))
                .create("Original Name")
                .rename("New Name")

            val loadedBudget = Budget(SimpleAggregateRoot(id = id, facts = budget.changes()))


            assertThat(loadedBudget.name, equalTo("New Name"))
        }
    }

    context("add account") {
        test("adds the new account") {
            val aggregateRoot = SimpleAggregateRoot<BudgetEvent>(id)

            val account = Budget(aggregateRoot)
                .create("My first budget")
                .addAccount("Checking")

            assertThat(
                account,
                has(Account::name, equalTo("Checking")) and
                        has(Account::budgetId, equalTo(id))
            )
        }
    }
})

fun <T> published(event: T): Matcher<AggregateRoot<T>> {
    return has("published event", { it.changes() }, hasElement(event))
}