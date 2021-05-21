package net.syntessense.app.gandiforwardmanager

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import net.syntessense.app.gandiforwardmanager.databinding.EditActivityBinding


class EditActivity : AppCompatActivity() {

    private lateinit var binding: EditActivityBinding
    private var checkboxes = ArrayList<CheckBox>()
    private lateinit var domain : String
    private lateinit var source : String
    private lateinit var api: GandiApi
    private var isNew = false
    var ctx = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mainForwards = Target.parseList(
            PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("faddresses", "") ?: ""
        )

        api = getApi()
        isNew = intent.getBooleanExtra("new", false)
        domain = intent.getStringExtra("domain") ?: "ERROR"
        source = intent.getStringExtra("source") ?: ""
        val destinations = intent.getStringArrayListExtra("destinations") ?: ArrayList()

        title = if (isNew) "<new>@$domain" else "$source@$domain"

        binding.editDomain.text = domain
        binding.editSource.setText(source)

        var ckb : CheckBox
        for (i in mainForwards) {
            ckb = CheckBox(this)
            ckb.text = i.address
            ckb.isChecked = (isNew && i.selected) || destinations.contains(i.address)
            binding.editFields.addView(ckb)
            checkboxes.add(ckb)
        }

        var exists : Boolean
        var secondaryForwards = ""
        for (i in destinations) {
            exists = false
            for (j in mainForwards)
                exists = exists || (j.address == i)
            if (!exists)
                secondaryForwards += (if (secondaryForwards == "") "" else "\n") + i
        }

        binding.editTarget.setText(secondaryForwards)
        binding.fab.setOnClickListener {
            if (isNew) {
                api.notify("Creating ...")
                api.createAddress(
                    domain,
                    binding.editSource.text.toString(),
                    getTargets()
                )
            } else {
                api.notify("Saving ...")
                api.updateAddress(domain, source, getTargets())
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(if (isNew) R.menu.edit_new else R.menu.edit_edit, menu)
        return true
    }

    private fun getTargets(): ArrayList<String> {
        val destinations: ArrayList<String> = ArrayList()
        val otherDestinations = findViewById<EditText>(R.id.editTarget).text.toString().split("\n").toTypedArray()
        for (c in checkboxes)
            if (c.isChecked)
                destinations.add(c.text.toString())
        for (d in otherDestinations)
            if (d.trim().isNotEmpty())
                destinations.add(d.trim())
        return destinations
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cancel -> {
                setResult(RESULT_CANCELED)
                finish()
                true
            }
            R.id.action_delete -> {
                api.notify("Deleting ...")
                api.deleteAddress(domain, source)
                true
            }
            R.id.action_create -> {
                api.notify("Creating ...")
                api.createAddress(domain, binding.editSource.text.toString(), getTargets())
                true
            }
            R.id.action_update -> {
                api.notify("Saving ...")
                api.updateAddress(domain, source, getTargets())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getApi(): GandiApi {
        return object: GandiApi(ctx) {
            override fun notify(msg: String) {
                Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
            }
            override fun onCreateReady() {
                setResult(RESULT_OK)
                finish()
            }
            override fun onPutReady() {
                setResult(RESULT_OK)
                finish()
            }
            override fun onDeleteReady() {
                setResult(RESULT_OK)
                finish()
            }
        }
    }

}