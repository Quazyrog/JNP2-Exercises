package pl.edu.mimuw.students.wm382710.jnp.task02

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog

class RadioFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ui = inflater.inflate(R.layout.fragment_radio, container, false)
        val radioGroup = ui.findViewById<RadioGroup>(R.id.radio_group)
        val c = context
        if (c === null)
            return null
        radioGroup.setOnCheckedChangeListener { _, id ->
            val builder = AlertDialog.Builder(c)
            builder.setMessage("Selected $id!")
                .setPositiveButton("YAY!", DialogInterface.OnClickListener { _, _ -> })
                .create().show()
        }
        return ui
    }

}
