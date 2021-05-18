package net.syntessense.app.gandiforwardmanager

import android.content.Intent
import android.os.Bundle
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
    private lateinit var layout: SwipeRefreshLayout
    private var addresses = ArrayList<Address>()
    var ctx = this;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddressesActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);


        layout = findViewById(R.id.addresses_refresher)
        layout.setOnRefreshListener(this)

        var targets = ArrayList<String>()
        targets.add("tgt1")
        targets.add("tgt2")
        targets.add("tgt3")
        addresses.add(Address("source1", targets, "href"))
        addresses.add(Address("source2", targets, "href"))
        addresses.add(Address("source3", targets, "href"))
        addresses.add(Address("source4", targets, "href"))

        var recyclerView = findViewById<RecyclerView>(R.id.addresses_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = this.getAdapter()


        /*
        val navController = findNavController(R.id.nav_host_fragment_content_addresses)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        */


        val domain = intent.getStringExtra("domain")
        title = domain

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onRefresh() {
        layout.isRefreshing = false;
    }


    private fun getAdapter():ListAdapter<Address> {
        return object : ListAdapter<Address>(ctx) {

            override fun getData(): List<Address> {
                return addresses;
            }

            override fun getItemCount(): Int {
                return addresses.size;
            }

            override fun getItemView(): Int {
                return R.layout.address;
            }

            override fun onItemClick(v: View, p: Int) {
                /*
                var intent = Intent(ctx, AddressesActivity::class.java)
                var b = Bundle()
                b.putString("address", addresses.get(p).source)
                intent.putExtras(b)
                startActivity(intent)
                */
            }

            override fun setRepresentation(v: RecyclerView.ViewHolder, p: Int) {
                v.itemView.findViewById<TextView>(R.id.source).setText(addresses.get(p).source)
                v.itemView.findViewById<TextView>(R.id.target).setText(
                    addresses.get(p).destinations.joinToString("\n")
                )
            }

        }
    }

}