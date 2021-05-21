package net.syntessense.app.gandiforwardmanager

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import net.syntessense.app.gandiforwardmanager.databinding.AddressesActivityBinding

class AddressesActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: AddressesActivityBinding
    private lateinit var domain : String
    private lateinit var adapter: ListAdapter<Address>
    private lateinit var api: GandiApi
    private var addresses = ArrayList<Address>()
    var ctx = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddressesActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        api = this.getApi()
        adapter = this.getAdapter()

        binding.addressesRefresher.setOnRefreshListener(this)
        binding.addressesList.layoutManager = LinearLayoutManager(this)
        binding.addressesList.adapter = adapter
        domain = intent.getStringExtra("domain") ?: "ERROR"
        title = domain

        binding.fab.setOnClickListener {
            val intent = Intent(ctx, EditActivity::class.java)
            val b = Bundle()
            b.putBoolean("new", true)
            b.putString("domain", domain)
            intent.putExtras(b)
            startActivityForResult(intent, 0)
        }

        api.getAddresses(domain)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
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
                b.putString("source", addresses[p].source)
                b.putStringArrayList("destinations", addresses[p].destinations)
                intent.putExtras(b)
                startActivityForResult(intent, 0)
            }
        }
    }

    private fun getApi(): GandiApi {
        return object: GandiApi(ctx) {
            override fun notify(msg: String) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
            }
            override fun onAddressesQuery() {
                binding.addressesRefresher.isRefreshing = true
            }
            override fun onAddressesReady(addresses: ArrayList<Address>) {
                ctx.addresses.clear()
                for (a in addresses)
                    ctx.addresses.add(a)
                adapter.notifyDataSetChanged()
                binding.addressesRefresher.isRefreshing = false
            }
        }
    }

}