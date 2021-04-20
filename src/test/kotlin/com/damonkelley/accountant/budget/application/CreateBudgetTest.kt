import com.damonkelley.accountant.budget.application.CreateBudgetHandler
import com.damonkelley.accountant.budget.application.NewBudgetProvider
import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.trace
import com.damonkelley.accountant.tracing.EventTrace
import com.damonkelley.accountant.tracing.Trace
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CreateBudgetHandlerTest {
    @Test
    fun `it returns a result`() {
        val budget = mockk<Budget>(relaxed = true)

        val actual = CreateBudgetHandler(InMemoryBudgetProvider(budget))
            .handle(trace(), CreateBudget(name = "A new budget"))

        assertEquals(Result.success(Unit), actual)
    }

    @Test
    fun `it creates the budget`() {
        val budget = mockk<Budget>(relaxed = true)

        val provider = InMemoryBudgetProvider(budget)
        CreateBudgetHandler(provider)
            .handle(trace(), CreateBudget(name = "A new budget"))

        verify { budget.create("A new budget") }
    }
}

class InMemoryBudgetProvider(private val stub: Budget) : NewBudgetProvider {
    override fun new(trace: EventTrace, block: (Budget) -> Budget): Result<Unit> {
        return stub
            .let(block)
            .let { Result.success(Unit) }
    }
}