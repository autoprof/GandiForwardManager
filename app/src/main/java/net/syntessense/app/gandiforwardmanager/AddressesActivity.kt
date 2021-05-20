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
import net.syntessense.app.gandiforwardmanager.databinding.AddressesActivityBinding


class AddressesActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: AddressesActivityBinding
    private lateinit var layout: SwipeRefreshLayout
    private lateinit var domain : String
    private var addresses = ArrayList<Address>()
    var ctx = this

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddressesActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val targets = ArrayList<String>()
        targets.add("tgt1")
        targets.add("tgt2")
        targets.add("tgt3")
        targets.add("tgt4")
        targets.add("tgt5")
        targets.add("tgt6")
        addresses.add(Address("source1", targets, "href"))
        addresses.add(Address("source2", targets, "href"))
        addresses.add(Address("source3", targets, "href"))
        addresses.add(Address("source4", targets, "href"))

        binding.addressesRefresher.setOnRefreshListener(this)
        binding.addressesList.layoutManager = LinearLayoutManager(this)
        binding.addressesList.adapter = this.getAdapter()
        domain = intent.getStringExtra("domain") ?: "ERROR"
        title = domain

        binding.fab.setOnClickListener {
            val intent = Intent(ctx, EditActivity::class.java)
            val b = Bundle()
            b.putBoolean("new", true)
            b.putString("domain", domain)
            intent.putExtras(b)
            startActivity(intent)
        }

    }

    override fun onRefresh() {
        layout.isRefreshing = false
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

            override fun onItemClick(v: View, p: Int) {
                val intent = Intent(ctx, EditActivity::class.java)
                val b = Bundle()
                b.putBoolean("new", false)
                b.putString("domain", domain)
                b.putString("source", addresses[p].source)
                b.putStringArrayList("destinations", addresses[p].destinations)
                intent.putExtras(b)
                startActivity(intent)
            }

            override fun setRepresentation(v: RecyclerView.ViewHolder, p: Int) {
                v.itemView.findViewById<TextView>(R.id.source).text = addresses[p].source
                v.itemView.findViewById<TextView>(R.id.target).text =
                    addresses[p].destinations.joinToString("\n")
            }

        }
    }

}