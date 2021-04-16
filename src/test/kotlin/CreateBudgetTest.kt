import com.damonkelley.accountant.budget.Budget
import com.damonkelley.accountant.budget.BudgetCreated
import com.damonkelley.accountant.budget.BudgetEvent
import com.damonkelley.accountant.budget.CreateBudget
import com.damonkelley.accountant.budget.CreateBudgetHandler
import com.damonkelley.accountant.infrastructure.Command
import com.damonkelley.accountant.infrastructure.Context
import com.damonkelley.accountant.infrastructure.Event
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class CreateBudgetHandlerTest {
    @Test
    fun `it works`() {
        val id = UUID.randomUUID()
        val context = Context(id, "stream")


        val actual = CreateBudgetHandler().handle( CreateBudget(name = "A new budget"))

        val expected = Budget.create(name = "A new budget")
        assertEquals(expected.name, actual.name)
    }
}

