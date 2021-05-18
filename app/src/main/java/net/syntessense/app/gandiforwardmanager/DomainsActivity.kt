package net.syntessense.app.gandiforwardmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import net.syntessense.app.gandiforwardmanager.databinding.DomainsActivityBinding

class DomainsActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: DomainsActivityBinding
    private lateinit var layout: SwipeRefreshLayout
    private var domains = ArrayList<Domain>()
    var ctx = this;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DomainsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        layout = findViewById(R.id.domains_refresher)
        layout.setOnRefreshListener(this)

        domains.add(Domain("hello", "hello"))
        domains.add(Domain("hello2", "hello2"))
        domains.add(Domain("hello3", "hello3"))

        var recyclerView = findViewById<RecyclerView>(R.id.domains_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = this.getAdapter()

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

    }

    override fun onRefresh() {
        layout.isRefreshing = false;
    }

    private fun getAdapter():ListAdapter<Domain> {
        return object : ListAdapter<Domain>(ctx) {

            override fun getData(): List<Domain> {
                return domains;
            }

            override fun getItemCount(): Int {
                return domains.size;
            }

            override fun getItemView(): Int {
                return R.layout.domain;
            }

            override fun onItemClick(v: View, p: Int) {
                var intent = Intent(ctx, AddressesActivity::class.java)
                var b = Bundle()
                b.putString("domain", domains.get(p).fqdn)
                intent.putExtras(b)
                startActivity(intent)
            }

            override fun setRepresentation(v: RecyclerView.ViewHolder, p: Int) {
                v.itemView.findViewById<TextView>(R.id.domain).setText(domains.get(p).fqdn)
            }

        }
    }

}