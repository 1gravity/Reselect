package org.reduxkotlin.select.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import org.reduxkotlin.*
import org.reduxkotlin.select.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var subscription: StoreSubscription? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController(this).navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        subscription = store.select({ it.counter }) { counter ->
            view.findViewById<TextView>(R.id.textview_first).text =
                getString(R.string.hello_first_fragment, counter.toString())
        }
    }

    override fun onDetach() {
        super.onDetach()
        subscription?.invoke()
    }
}
