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
    var onSelect: ((Int) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vignette, container, false)
    }

    fun onSelect(callback: (Int) -> Unit) {
        onSelect = callback
    }

    fun showVignette(vignette: Vignette) {
        vignetteImageView.setImageBitmap(vignette.image)
        vignetteDescription.text = vignette.description

        vignetteChoices.removeAllViews()
        vignette.choices.forEachIndexed() { index, choice ->
            val radio = RadioButton(context)
            radio.text = choice.text
            radio.setOnClickListener { onSelect?.let { it1 -> it1(index) } }
            vignetteChoices.addView(radio)
        }
    }
}