package dev.niza.autodroid

import android.content.Context
import androidx.test.core.app.ApplicationProvider
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
}
