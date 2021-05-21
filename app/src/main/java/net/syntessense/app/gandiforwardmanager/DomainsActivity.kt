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
    private var apis = ArrayList<GandiApi>()
    private lateinit var originalKey : String
    private var apisReady = 0
    private var rawDomains = ArrayList<Domain>()
    private var domains = ArrayList<Domain>()
    var ctx = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DomainsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = this.getAdapter()
        originalKey = PreferenceManager
            .getDefaultSharedPreferences(ctx)
            .getString("apiKey", "") ?: ""

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
        val sp = PreferenceManager.getDefaultSharedPreferences(ctx)
        val newKey = sp.getString("apiKey", "") ?: ""
        if (newKey == originalKey)
            refreshDomains()
        else {
            originalKey = newKey
            onRefresh()
        }
    }

    override fun onRefresh() {
        apis.clear()
        val sp = PreferenceManager.getDefaultSharedPreferences(ctx)
        for (ak in (sp.getString("apiKey", "") ?: "").split("\n"))
            if (ak.trim().isNotEmpty())
                apis.add(this.getApi(ak.trim()))
        if (apis.size == 0)
            return
        binding.domainsRefresher.isRefreshing = true
        apisReady = 0
        rawDomains = ArrayList()
        for(api in apis)
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
                b.putString("apiKey", domains[p].apiKey)
                b.putString("domain", domains[p].fqdn)
                intent.putExtras(b)
                startActivity(intent)
            }
        }
    }

    private fun getApi(apiKey: String): GandiApi {
        return object: GandiApi(apiKey, ctx) {
            override fun notify(msg: String) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
            }
            override fun onDomainsReady(domains: ArrayList<Domain>) {
                for (d in domains)
                    rawDomains.add(d)
                apisReady++
                if (apisReady == apis.size) {
                    refreshDomains()
                    binding.domainsRefresher.isRefreshing = false
                }
            }
        }
    }

}