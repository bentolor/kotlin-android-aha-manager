package de.bentolor.katamanager

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.text.format.DateFormat
import android.view.ActionMode
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AbsListView.MultiChoiceModeListener
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.TextView

import de.bentolor.ahamanager.R

class AhaListFragment : ListFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity.setTitle(R.string.aha_title)
        val ahas = AhaLab[activity].ahas

        listAdapter = AhaAdapter(ahas)

        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_aha_list, container, false)
        val createAhaButton = v.findViewById(R.id.fragment_crime_create) as Button
        createAhaButton.setOnClickListener { showCreateAha() }

        val listView = v.findViewById(android.R.id.list) as ListView

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // Use floating context menus on Froyo and Gingerbread
            registerForContextMenu(listView)
        } else {
            // Use contextual action bar on Honeycomb and higher
            listView.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE_MODAL
            listView.setMultiChoiceModeListener(object : MultiChoiceModeListener {

                override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode) {
                }

                override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                    val inflater = mode.menuInflater
                    inflater.inflate(R.menu.aha_list_item_context, menu)
                    return true
                }

                override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.menu_item_delete_crime -> {
                            val adapter = listAdapter as AhaAdapter
                            val ahaLab = AhaLab.get(activity)
                            for (i in adapter.count - 1 downTo 0) {
                                if (getListView().isItemChecked(i)) {
                                    ahaLab.deleteAha(adapter.getItem(i))
                                }
                            }
                            mode.finish()
                            adapter.notifyDataSetChanged()
                            ahaLab.saveAhas()
                            return true
                        }
                        else -> return false
                    }

                }

                override fun onItemCheckedStateChanged(mode: ActionMode, position: Int,
                                                       id: Long, checked: Boolean) {
                }
            })
        }

        return v
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        val aha = (listAdapter as AhaAdapter).getItem(position)
        val i = Intent(activity, AhaPagerActivity::class.java)
        i.putExtra(AhaFragment.EXTRA_AHA_ID, aha!!.id)
        startActivity(i)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val info = item!!.menuInfo as AdapterContextMenuInfo
        val position = info.position
        val adapter = listAdapter as AhaAdapter
        val aha = adapter.getItem(position)

        when (item.itemId) {
            R.id.menu_item_delete_crime -> {
                val ahaLab = AhaLab.get(activity)
                ahaLab.deleteAha(aha)
                adapter.notifyDataSetChanged()
                ahaLab.saveAhas()
                return true
            }
        }

        return super.onContextItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        (listAdapter as AhaAdapter).notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.fragment_aha_list, menu)
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_item_new_crime -> {
                showCreateAha()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenuInfo) {
        activity.menuInflater.inflate(R.menu.aha_list_item_context, menu)
    }

    private fun showCreateAha() {
        val aha = Aha()
        AhaLab.get(activity).addAha(aha)
        val i = Intent(activity, AhaPagerActivity::class.java)
        i.putExtra(AhaFragment.EXTRA_AHA_ID, aha.id)
        startActivityForResult(i, 0)
    }

    private inner class AhaAdapter(ahas: List<Aha>) : ArrayAdapter<Aha>(activity, 0, ahas) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView = activity.layoutInflater.inflate(R.layout.list_item_aha, parent, false)
            }

            // Configure for this crime
            val aha = getItem(position)

            val titleTextView = convertView!!.findViewById(R.id.crime_list_item_titleTextView) as TextView
            titleTextView.text = aha!!.title

            val dateTextView = convertView.findViewById(R.id.crime_list_item_dateTextView) as TextView
            val cs = DateFormat.format("EEEE, MMM dd, yyyy", aha.date)
            dateTextView.text = cs

            val solvedCheckedBox = convertView.findViewById(R.id.aha_list_item_usefulCheckBox) as CheckBox
            solvedCheckedBox.isChecked = aha.isUseful

            return convertView
        }
    }

}