package com.damonkelley.accountant.budget.domain

import com.damonkelley.accountant.eventsourcing.WritableAggregateRoot
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.hasElement
import org.spekframework.spek2.Spek
import java.util.UUID

class BudgetTest : Spek({
    group("create") {
        test("creates a new budget") {
            val aggregateRoot = AggregateRootForTesting()
            Budget(aggregateRoot).create("My first budget")

            assertThat(aggregateRoot, published(BudgetCreated("My first budget")))
        }
    }

    group("rename") {
        test("renames the budget") {
            val aggregateRoot = AggregateRootForTesting()

            Budget(aggregateRoot)
                .create("My first budget")
                .rename("My budget")

            assertThat(aggregateRoot, published(BudgetRenamed("My budget")))
        }
    }
})

fun published(event: BudgetEvent): Matcher<AggregateRootForTesting> {
    return has("published event", { it.events }, hasElement(event))
}

class AggregateRootForTesting(override val id: UUID = UUID.nameUUIDFromBytes("AggregateRootForTesting".toByteArray())) :
    WritableAggregateRoot<BudgetEvent> {

    val events = mutableListOf<BudgetEvent>()

    override fun raise(event: BudgetEvent): WritableAggregateRoot<BudgetEvent> {
        return apply { events.add(event) }
    }

    override fun facts(consumer: (BudgetEvent) -> Unit): WritableAggregateRoot<BudgetEvent> {
        return this
    }
}