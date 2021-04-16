import com.damonkelley.accountant.budget.Budget
import com.damonkelley.accountant.budget.BudgetCreated
import com.damonkelley.accountant.budget.BudgetEvent
import com.damonkelley.accountant.budget.CreateBudget
import com.damonkelley.accountant.budget.CreateBudgetHandler
import com.damonkelley.accountant.eventsourcing.Repository
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.hasElement
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class CreateBudgetHandlerTest {
    @Test
    fun `it returns a result`() {
        val actual = CreateBudgetHandler(InMemoryBudgetRepository())
                .handle( CreateBudget(name = "A new budget"))

        assertEquals(Result.success(Unit), actual)
    }

    @Test
    fun `it creates the budget`() {
        val repository = InMemoryBudgetRepository()
        CreateBudgetHandler(repository)
                .handle(CreateBudget(name = "A new budget"))

        assertThat(repository.db, hasEvent(BudgetCreated("A new budget")))
    }
}

fun hasEvent(event: BudgetEvent) : Matcher<Map<UUID, List<BudgetEvent>>>  {
    return has("an event", { it.values.flatten() }, hasElement(event))
}

class InMemoryBudgetRepository: Repository<Budget> {
    val db = mutableMapOf<UUID, List<BudgetEvent>>()

    override fun load(id: UUID): Budget? {
        TODO("Not yet implemented")
    }

    override fun save(budget: Budget): Result<Budget> {
        db[budget.id] = budget.changes

        return Result.success(budget)
    }
}

