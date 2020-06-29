package pl.edu.mimuw.students.wm382710.appa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView

class VignetteFragment(private val hero: HeroWithInventory) : Fragment() {
    private var onSelect: ((Int) -> Unit)? = null
    private var vig: Vignette? = null

    private var hasView: Boolean = false
    private var vignetteImageView: ImageView? = null
    private var vignetteDescription: TextView? = null
    private var vignetteChoices: RadioGroup? = null

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
        val ui = inflater.inflate(R.layout.fragment_vignette, container, false)
        vignetteImageView = ui.findViewById(R.id.vignetteImageView)
        vignetteDescription = ui.findViewById(R.id.vignetteDescription)
        vignetteChoices = ui.findViewById(R.id.vignetteChoices)
        hasView = true
        return ui
    }

    override fun onDestroyView() {
        hasView = false
        vignetteImageView = null
        vignetteDescription = null
        vignetteChoices = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        showVignette()
    }

    fun onSelect(callback: (Int) -> Unit) {
        onSelect = callback
    }

    private fun showVignette() {
        if (!hasView)
            return

        if (vig!!.image !== null)
            vignetteImageView!!.setImageBitmap(vig!!.image)
        vignetteDescription!!.text = vig!!.description

        vig!!.grantItem?.let {
            if (!hero.hasItem(it))
                hero.newItems.add(Item(0, hero.hero.heroId, it, ""))
        }

        vignetteChoices!!.removeAllViews()
        vig!!.choices.forEachIndexed() { index, choice ->
            if (!choice.requirements.check(hero))
                return@forEachIndexed
            val radio = RadioButton(context)
            radio.text = choice.text
            radio.setOnClickListener { onSelect?.let { it1 -> it1(index) } }
            vignetteChoices!!.addView(radio)
        }
    }
}