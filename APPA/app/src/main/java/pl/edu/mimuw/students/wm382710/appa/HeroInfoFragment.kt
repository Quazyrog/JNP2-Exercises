package pl.edu.mimuw.students.wm382710.appa

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ItemsRecyclerAdapter(private val context: Context): RecyclerView.Adapter<ItemsRecyclerAdapter.ViewHolder>() {

    var dataSource: HeroWithInventory? = null

    class ViewHolder(private val root: TextView): RecyclerView.ViewHolder(root) {
        private var myItem: Item? = null

        var item: Item
            get() = myItem ?: throw IllegalStateException("No item was assigned yet to this view")
            set(h) {
                myItem = h
                root.text = item.name
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TextView(context))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position >= hero.newItems.size)
            holder.item = hero.items[position - hero.newItems.size]
        else
            holder.item = hero.newItems[position]
    }

    override fun getItemCount() = if (dataSource !== null) hero.items.size + hero.newItems.size else 0

    var hero: HeroWithInventory
        get() = dataSource ?: throw IllegalStateException("No hero assigned")
        set(value) {
            dataSource = value
            MainScope().launch { notifyDataSetChanged() }
        }
}


class HeroInfoFragment : Fragment() {
    private var shownHero: HeroWithInventory = HeroWithInventory(Hero(0, "", 4, 5, 3, 999), ArrayList())
    private lateinit var itemsAdapter: ItemsRecyclerAdapter

    var hero: HeroWithInventory
        get() = shownHero
        set(v) {
            shownHero = v
            view?.let { showValues(it) }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        itemsAdapter = ItemsRecyclerAdapter(context!!)
        return inflater.inflate(R.layout.fragment_hero_info, container, false).let {
            it.findViewById<RecyclerView>(R.id.recycledItems).apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                findViewById<RecyclerView>(R.id.recycledItems).adapter = itemsAdapter
            }
            showValues(it)
        }
    }

    private fun showValues(view: View) = view.apply {
        findViewById<TextView>(R.id.heroNameText).text = shownHero.hero.heroName
        findViewById<TextView>(R.id.strengthText).text = shownHero.hero.strength.toString()
        findViewById<TextView>(R.id.dexterityText).text = shownHero.hero.dexterity.toString()
        findViewById<TextView>(R.id.intelligenceText).text = shownHero.hero.intelligence.toString()
        findViewById<TextView>(R.id.constitutionText).text = shownHero.hero.constitution.toString()
        itemsAdapter.hero = shownHero
    }
}