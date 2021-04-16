import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBConnectionString
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.eventstore.dbclient.NackAction
import com.eventstore.dbclient.PersistentSubscription
import com.eventstore.dbclient.PersistentSubscriptionListener
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.SubscribeToAllOptions
import com.eventstore.dbclient.Subscription
import com.eventstore.dbclient.SubscriptionFilter
import com.eventstore.dbclient.SubscriptionListener
import io.github.serpro69.kfaker.Faker
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

open class Traceable<T>(
        val id: UUID,
        val correlationId: UUID,
        val causationId: UUID,
        val data: T,
) {

    fun <R> map(function: (Traceable<T>) -> R): Traceable<R> {
        val newData = function(this)
        return Traceable(
                id = UUID.randomUUID(),
                correlationId = correlationId,
                causationId = id,
                data = newData
        )
    }
}

class Command<T>(id: UUID, correlationId: UUID, causationId: UUID, data: T) : Traceable<T>(id, correlationId, causationId, data)

class CommandHandlers(private val repository: BudgetRepository) {
    fun handle(command: Command<Budget.Command.CreateBudget>) {
        val event = command.map {
            Budget.Event.BudgetCreated(id = it.id, name = it.data.name)
        }
        repository.save(Budget().apply(event))
    }
}

class BudgetRepository(private val eventStore: EventStoreDBClient) {
    private fun Traceable<out Budget.Event>.toEventData(): EventData = when (data) {
        is Budget.Event.BudgetCreated -> EventData(
                this.id,
                "BudgetCreated",
                "application/json",
                Json.encodeToString(this.data).toByteArray(),
                Json.encodeToString(UserMetadata(this.correlationId, this.causationId)).toByteArray(),
        )
    }

    fun save(budget: Budget): Budget {
        val proposedMessage = budget.changes.map { it.toEventData() }

        eventStore.appendToStream("budget-${budget.id}", proposedMessage.iterator())

        return budget
    }
}

class Budget(
        val id: UUID = UUID.randomUUID(),
        private val name: String = "",
        override val changes: List<Traceable<out Event>> = emptyList(),
) : AggregateRoot<Budget.Event> {
    companion object {}

    fun apply(event: Traceable<Event.BudgetCreated>): Budget {
        return Budget(id = event.id, name = event.data.name, changes = listOf(*this.changes.toTypedArray(), event))
    }

    sealed class Event {
        @Serializable
        data class BudgetCreated(
                @Serializable(with = UUIDSerializer::class)
                val id: UUID,
                val name: String,
        ) : Event()
    }


    sealed class Command {
        @Serializable
        data class CreateBudget(val name: String) : Command()
    }
}

interface AggregateRoot<out T> {
    val changes: List<Traceable<out T>>
}

fun main(args: Array<String>) {
    val settings = EventStoreDBConnectionString.parseOrThrow("esdb://localhost:2113?tls=false")
    val eventStore = EventStoreDBClient.create(settings).apply {
        val withoutSystemStreams = SubscriptionFilter.newBuilder()
                .withStreamNameRegularExpression("^((?!\\$)+).*$")
                .build()

        subscribeToAll(Listener(), SubscribeToAllOptions.get().filter(withoutSystemStreams))
    }

    EventStoreDBPersistentSubscriptionsClient.create(settings).apply {
        val commandHandlers = CommandHandlers(BudgetRepository(eventStore = eventStore))
//        create("budget:commands", "app")
        subscribe("budget:commands", "app", CommandListener(commandHandlers))
    }

    val id = UUID.randomUUID()
    val faker = Faker()
    val command = buildJsonObject {
        put("name", faker.rupaul.queens())
    }
    eventStore.appendToStream("budget:commands",
            EventData(
            id,
            "CreateBudget",
            "application/json",
            command.toString().toByteArray(),
            Json.encodeToString(UserMetadata(id)).toByteArray()
    ))

    readLine()
}

@Serializable
data class UserMetadata(
        @SerialName("\$correlationId")
        @Serializable(with = UUIDSerializer::class)
        val correlationId: UUID,
        @SerialName("\$causationId")
        @Serializable(with = UUIDSerializer::class)
        val causationId: UUID,
) {
    constructor(id: UUID) : this(id, id)

    companion object {
        fun from(byteArray: ByteArray): UserMetadata? {
            return try {
                Json.decodeFromString(String(byteArray))
            } catch (e: Throwable) {
                println(e)
                null
            }
        }
    }
}

class CommandListener(private val commandHandlers: CommandHandlers) : PersistentSubscriptionListener() {
    override fun onEvent(subscription: PersistentSubscription, event: ResolvedEvent) {
        when (event.event.eventType) {
            "CreateBudget" -> {
                try {
                    val command = Json.decodeFromString<Budget.Command.CreateBudget>(String(event.event.eventData))
                    val metadata = UserMetadata.from(event.event.userMetadata)

                    commandHandlers.handle(Command(
                            id = event.event.eventId,
                            correlationId = metadata?.correlationId ?: event.event.eventId,
                            causationId = event.event.eventId,
                            data = command
                    ))
                } catch (e: Throwable) {
                    println(e)
                    subscription.nack(NackAction.Park, e.message, event)
                }
            }
        }

        subscription.ack(event)
    }

    override fun onError(subscription: PersistentSubscription?, throwable: Throwable?) {
        println("Oops! ${throwable?.message}")
    }
}

private class Listener : SubscriptionListener() {
    override fun onEvent(subscription: Subscription, event: ResolvedEvent) {
        println("${event.event.eventId} - ${event.event.eventType} - ${String(event.event.eventData)} - ${String(event.event.userMetadata)}")
    }
}