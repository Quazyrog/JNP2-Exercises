package pl.edu.mimuw.students.wm382710.appa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_vignette.*

class VignetteFragment : Fragment() {
    private var onSelect: ((Int) -> Unit)? = null
    private var vig: Vignette? = null
    private var hasView: Boolean = false

    var vignette: Vignette?
        get() = vig
        set(v: Vignette?) {
            vig = v
            showVignette()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vignette, container, false).also { hasView = true }
    }

    override fun onResume() {
        super.onResume()
        showVignette()
    }

    fun onSelect(callback: (Int) -> Unit) {
        onSelect = callback
    }

    private fun showVignette() {
        if (!hasView || vig === null)
            return

        if (vig!!.image !== null)
            vignetteImageView.setImageBitmap(vig!!.image)
        vignetteDescription.text = vig!!.description

        vignetteChoices.removeAllViews()
        vig!!.choices.forEachIndexed() { index, choice ->
            val radio = RadioButton(context)
            radio.text = choice.text
            radio.setOnClickListener { onSelect?.let { it1 -> it1(index) } }
            vignetteChoices.addView(radio)
        }
    }
}