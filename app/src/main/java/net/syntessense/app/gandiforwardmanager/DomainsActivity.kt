package net.syntessense.app.gandiforwardmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import net.syntessense.app.gandiforwardmanager.databinding.DomainsActivityBinding

class DomainsActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: DomainsActivityBinding
    private lateinit var layout: SwipeRefreshLayout
    private var domains = ArrayList<Domain>()
    var ctx = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DomainsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layout = findViewById(R.id.domains_refresher)
        layout.setOnRefreshListener(this)

        domains.add(Domain("hello", "hello"))
        domains.add(Domain("hello2", "hello2"))
        domains.add(Domain("hello3", "hello3"))

        val recyclerView = findViewById<RecyclerView>(R.id.domains_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = this.getAdapter()

        binding.fab.setOnClickListener {
            startActivity(Intent(ctx, SettingsActivity::class.java))
        }

    }

    override fun onRefresh() {
        layout.isRefreshing = false
    }

    private fun getAdapter():ListAdapter<Domain> {
        return object : ListAdapter<Domain>(ctx) {

            override fun getData(): List<Domain> {
                return domains
            }

            override fun getItemCount(): Int {
                return domains.size
            }

            override fun getItemView(): Int {
                return R.layout.domain
            }

            override fun onItemClick(v: View, p: Int) {
                val intent = Intent(ctx, AddressesActivity::class.java)
                val b = Bundle()
                b.putString("domain", domains[p].fqdn)
                intent.putExtras(b)
                startActivity(intent)
            }

            override fun setRepresentation(v: RecyclerView.ViewHolder, p: Int) {
                v.itemView.findViewById<TextView>(R.id.domain).text = domains[p].fqdn
            }

        }
    }

}