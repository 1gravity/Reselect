package org.reduxkotlin.select.demo

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.reduxkotlin.*
import org.reduxkotlin.select.*

class MainActivity : AppCompatActivity() {

    private lateinit var loadingIndicator: ProgressBar

    private var subscription: StoreSubscription? = null

    private var multiSubscription: StoreSubscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))
        loadingIndicator = findViewById(R.id.loading_indicator)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            store.dispatch(networkRequest())
            Snackbar.make(view, "Make a fake network request", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        subscription = store.select({ it.isLoading }) {
            loadingIndicator.visibility = if (it) View.VISIBLE else View.GONE
        }

        multiSubscription = store.selectors {
            select({ it.isLoading }) {
                Log.e("redux", "isLoading: $it")
                loadingIndicator.visibility = if (it) View.VISIBLE else View.GONE
            }
            select({ it.counter }) {
                Log.e("redux", "counter: $it")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.invoke()
        multiSubscription?.invoke()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

/**
 * A fake network request thunk.  Just delays then dispatches a LoadingCompleteAction
 */
private fun networkRequest(): Thunk<AppState> = { dispatch, _, _ ->
    dispatch(StartLoading)
    GlobalScope.launch {
        delay(5000L)
        withContext(Dispatchers.Main) {
            dispatch(LoadingComplete)
        }
    }
}

object IncrementAction
object StartLoading
object LoadingComplete

private val reducer: Reducer<AppState> = { state, action ->
    when (action) {
        StartLoading -> state.copy(isLoading = true)
        LoadingComplete -> state.copy(isLoading = false)
        IncrementAction -> state.copy(counter = state.counter + 1)
        else -> state
    }
}

val store = createStore(reducer, AppState(), applyMiddleware(createThunkMiddleware()))
