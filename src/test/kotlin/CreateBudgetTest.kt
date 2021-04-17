import com.damonkelley.accountant.budget.domain.Budget
import com.damonkelley.accountant.budget.domain.BudgetEvent
import com.damonkelley.accountant.budget.domain.CreateBudget
import com.damonkelley.accountant.budget.application.CreateBudgetHandler
import com.damonkelley.accountant.eventsourcing.Recordable
import com.damonkelley.accountant.eventsourcing.RecordableProvider
import com.damonkelley.accountant.eventsourcing.Repository
import com.damonkelley.accountant.eventsourcing.Command
import com.damonkelley.accountant.eventsourcing.Context
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.isEmpty
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class CreateBudgetHandlerTest {
    @Test
    fun `it returns a result`() {
        val context = Context(UUID.randomUUID(), "stream")
        val actual = CreateBudgetHandler(InMemoryBudgetRepository(), Recording())
                .handle(Command(context) { CreateBudget(name = "A new budget") })

        assertEquals(Result.success(Unit), actual)
    }

    @Test
    fun `it creates the budget`() {
        val context = Context(UUID.randomUUID(), "stream")
        val repository = InMemoryBudgetRepository()
        CreateBudgetHandler(repository, Recording())
                .handle(Command(context) { CreateBudget(name = "A new budget") })

        val isNotEmpty = isEmpty.not()

        assertThat(repository.saved, isNotEmpty)
    }
}

class InMemoryBudgetRepository: Repository<Budget> {
    val saved = mutableListOf<Budget>()

    override fun load(id: UUID): Budget? {
        TODO("Not yet implemented")
    }

    override fun save(budget: Budget): Result<Budget> {
        saved.add(budget)
        return Result.success(budget)
    }
}

class Recording: RecordableProvider<BudgetEvent>, Recordable<BudgetEvent> {
    override fun record(event: BudgetEvent): Recordable<BudgetEvent> = this
    override fun replayChanges(apply: (BudgetEvent) -> Unit) = this
    override fun replayFacts(apply: (BudgetEvent) -> Unit) = this
    override fun from(context: Context): Recordable<BudgetEvent> = this
}
