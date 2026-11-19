package dev.niza.autodroid

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.niza.autodroid.engine.DataSmsReceivedTrigger
import dev.niza.autodroid.engine.NotifyAction
import dev.niza.autodroid.engine.Rule
import dev.niza.autodroid.engine.RuleStore
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class DataSmsRegistrarTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @AfterTest
    fun tearDown() {
        DataSmsRegistrar.resetForTest(context)
    }

    @Test
    fun `refresh with no rules file registers no ports`() {
        DataSmsRegistrar.refresh(context)

        assertEquals(emptySet(), DataSmsRegistrar.registeredPortsForTest())
    }

    @Test
    fun `refresh with invalid rules file registers no ports`() {
        context.openFileOutput("rules.json", Context.MODE_PRIVATE).use {
            it.write("invalid".toByteArray())
        }

        DataSmsRegistrar.refresh(context)

        assertEquals(emptySet(), DataSmsRegistrar.registeredPortsForTest())
    }

    @Test
    fun `refresh registers enabled data sms ports`() {
        writeRules(
            listOf(
                rule(id = "r1", port = 1234, enabled = true),
                rule(id = "r2", port = 5678, enabled = false),
            ),
        )

        DataSmsRegistrar.refresh(context)

        assertEquals(setOf(1234), DataSmsRegistrar.registeredPortsForTest())
    }

    @Test
    fun `refresh unregisters removed and disabled ports`() {
        writeRules(listOf(rule(id = "r1", port = 1234, enabled = true)))
        DataSmsRegistrar.refresh(context)

        writeRules(
            listOf(
                rule(id = "r1", port = 1234, enabled = false),
                rule(id = "r2", port = 5678, enabled = true),
            ),
        )
        DataSmsRegistrar.refresh(context)

        assertEquals(setOf(5678), DataSmsRegistrar.registeredPortsForTest())
    }

    private fun writeRules(rules: List<Rule>) {
        context.openFileOutput("rules.json", Context.MODE_PRIVATE).use {
            it.write(RuleStore().encode(rules).toByteArray())
        }
    }

    private fun rule(
        id: String,
        port: Int,
        enabled: Boolean,
    ): Rule =
        Rule(
            id = id,
            name = id,
            trigger = DataSmsReceivedTrigger("trigger-$id", destinationPort = port),
            actions = listOf(NotifyAction("action-$id", title = "Title", body = "Body")),
            enabled = enabled,
        )
}
