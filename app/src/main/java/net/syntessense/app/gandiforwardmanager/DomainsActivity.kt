package net.syntessense.app.gandiforwardmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import net.syntessense.app.gandiforwardmanager.databinding.DomainsActivityBinding

class DomainsActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: DomainsActivityBinding
    private lateinit var adapter: ListAdapter<Domain>
    private lateinit var api: GandiApi
    private var rawDomains = ArrayList<Domain>()
    private var domains = ArrayList<Domain>()
    var ctx = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DomainsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        api = this.getApi()
        adapter = this.getAdapter()

        binding.domainsRefresher.setOnRefreshListener(this)
        binding.domainsList.layoutManager = LinearLayoutManager(this)
        binding.domainsList.adapter = adapter
        binding.fab.setOnClickListener {
            startActivity(Intent(ctx, SettingsActivity::class.java))
        }

        onRefresh()

    }

    override fun onResume() {
        super.onResume()
        refreshDomains()
    }

    override fun onRefresh() {
        api.getDomains()
    }

    private fun refreshDomains() {
        val sp = PreferenceManager.getDefaultSharedPreferences(ctx)
        domains.clear()
        for (d in rawDomains)
            if (sp.getBoolean("show_domain_" + d.fqdn, false))
                domains.add(d)
        adapter.notifyDataSetChanged()
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
            override fun setRepresentation(v: RecyclerView.ViewHolder, p: Int) {
                v.itemView.findViewById<TextView>(R.id.domain).text = domains[p].fqdn
            }
            override fun onItemClick(v: View, p: Int) {
                val intent = Intent(ctx, AddressesActivity::class.java)
                val b = Bundle()
                b.putString("domain", domains[p].fqdn)
                intent.putExtras(b)
                startActivity(intent)
            }
        }
    }

    fun getApi(): GandiApi {
        return object: GandiApi(ctx) {
            override fun notify(msg: String) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
            }
            override fun onDomainsQuery() {
                binding.domainsRefresher.isRefreshing = true
            }
            override fun onDomainsReady(domains: ArrayList<Domain>) {
                rawDomains = domains
                refreshDomains()
                binding.domainsRefresher.isRefreshing = false
            }
        }
    }

}