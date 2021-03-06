package net.syntessense.app.gandiforwardmanager

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import net.syntessense.app.gandiforwardmanager.databinding.AddressesActivityBinding


class AddressesActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: AddressesActivityBinding
    private lateinit var domain : String
    private lateinit var apiKey : String
    private lateinit var adapter: ListAdapter<Address>
    private lateinit var api: GandiApi
    private var rawAddresses = ArrayList<Address>()
    private var addresses = ArrayList<Address>()
    private var filter = ""
    private lateinit var searchView : SearchView
    var ctx = this

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.isIconifiedByDefault = false
        searchView.setPadding(-150, 0, 0, 0)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                filter = newText ?: ""
                refreshAdresses()
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                filter = query ?: ""
                return true
            }
        })
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddressesActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        apiKey = intent.getStringExtra("apiKey") ?: "ERROR"
        domain = intent.getStringExtra("domain") ?: "ERROR"
        title = domain

        api = this.getApi()
        adapter = this.getAdapter()

        binding.addressesRefresher.setOnRefreshListener(this)
        binding.addressesList.layoutManager = LinearLayoutManager(this)
        binding.addressesList.adapter = adapter

        (object : ItemTouchHelper(
            Swiper(domain, api, addresses,0, LEFT + RIGHT)
        ){}).attachToRecyclerView(binding.addressesList)

        binding.fab.setOnClickListener {
            val intent = Intent(ctx, EditActivity::class.java)
            val b = Bundle()
            b.putBoolean("new", true)
            b.putString("domain", domain)
            b.putString("apiKey", apiKey)
            intent.putExtras(b)
            startActivityForResult(intent, 0)
        }

        api.getAddresses(domain)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> searchView.requestFocus()
            else -> finish()
        }
        return true
    }

    override fun onRefresh() {
        api.getAddresses(domain)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK)
            api.getAddresses(domain)
    }

    private fun getApi(): GandiApi {
        return object: GandiApi(apiKey, ctx) {
            override fun notify(msg: String) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
            }
            override fun onDeleteQuery() {
                Snackbar.make(binding.root, "Deleting...", Snackbar.LENGTH_LONG).show()
            }

            override fun onDeleteReady(position: Int) {
                rawAddresses.removeAt(position)
                adapter.notifyDataSetChanged()
                Snackbar.make(binding.root, "Deleted", Snackbar.LENGTH_LONG).show()
            }

            override fun onAddressesQuery() {
                binding.addressesRefresher.isRefreshing = true
            }

            override fun onAddressesReady(addresses: ArrayList<Address>) {
                rawAddresses.clear()
                for (a in addresses)
                    rawAddresses.add(a)
                refreshAdresses()
                binding.addressesRefresher.isRefreshing = false
            }
        }
    }


    private fun refreshAdresses() {
        val sp = PreferenceManager.getDefaultSharedPreferences(ctx)
        addresses.clear()
        for (d in rawAddresses)
            if (filter.trim() == "" || d.source.contains(filter.trim(), true) )
                addresses.add(d)
        adapter.notifyDataSetChanged()
    }

    private fun getAdapter():ListAdapter<Address> {
        return object : ListAdapter<Address>(ctx) {
            override fun getData(): List<Address> {
                return addresses
            }
            override fun getItemCount(): Int {
                return addresses.size
            }
            override fun getItemView(): Int {
                return R.layout.address
            }
            override fun setRepresentation(v: RecyclerView.ViewHolder, p: Int) {
                v.itemView.findViewById<TextView>(R.id.source).text = addresses[p].source
                v.itemView.findViewById<TextView>(R.id.target).text =
                    addresses[p].destinations.joinToString("\n")
            }
            override fun onItemClick(v: View, p: Int) {
                val intent = Intent(ctx, EditActivity::class.java)
                val b = Bundle()
                b.putBoolean("new", false)
                b.putString("domain", domain)
                b.putString("apiKey", apiKey)
                b.putString("source", addresses[p].source)
                b.putStringArrayList("destinations", addresses[p].destinations)
                intent.putExtras(b)
                startActivityForResult(intent, 0)
            }
        }
    }

    class Swiper(
        private var domain: String,
        private var api: GandiApi,
        private var addresses: ArrayList<Address>,
        dragDirs: Int,
        swipeDirs: Int
    ) : ItemTouchHelper.SimpleCallback(dragDirs,
        swipeDirs
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }
        override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
            api.deleteAddress(domain, addresses[vh.adapterPosition].source, vh.adapterPosition)
        }
    }

}