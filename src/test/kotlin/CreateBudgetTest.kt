import com.damonkelley.accountant.budget.application.CreateBudgetHandler
import com.damonkelley.accountant.budget.application.NewBudgetProvider
import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.CreateBudget
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CreateBudgetHandlerTest {
    @Test
    fun `it returns a result`() {
        val budget = mockk<Budget>(relaxed = true)

        val actual = CreateBudgetHandler(InMemoryBudgetProvider(budget))
                .handle(CreateBudget(name = "A new budget"))

        assertEquals(Result.success(Unit), actual)
    }

    @Test
    fun `it creates the budget`() {
        val budget = mockk<Budget>(relaxed = true)

        val provider = InMemoryBudgetProvider(budget)
        CreateBudgetHandler(provider)
                .handle(CreateBudget(name = "A new budget"))

        verify { budget.create("A new budget") }
    }
}

class InMemoryBudgetProvider(private val stub: Budget): NewBudgetProvider {
    override fun new(block: (Budget) -> Budget): Result<Budget> {
        return stub
                .let(block)
                .let { Result.success(it) }
    }
}