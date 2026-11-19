package dev.niza.autodroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.niza.autodroid.engine.LinkOpenedEvent

class LinkRouterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handle(intent)
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handle(intent)
        finish()
    }

    private fun handle(intent: Intent?) {
        val uri = intent?.data ?: return
        Runtime.initialize(this)
        Runtime.loadRules(this)
        Runtime.engine.dispatch(
            LinkOpenedEvent(
                scheme = uri.scheme,
                host = uri.host,
                path = uri.path,
            ),
        )
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_VIEW, uri),
                "Open link",
            ),
        )
    }
}
