package net.syntessense.app.gandiforwardmanager


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import net.syntessense.app.gandiforwardmanager.databinding.EditActivityBinding


class EditActivity : AppCompatActivity() {

    private lateinit var binding: EditActivityBinding
    private var ckbs = ArrayList<CheckBox>()
    private var isNew = false

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


        isNew = intent.getBooleanExtra("new", false)
        val domain = intent.getStringExtra("domain") ?: "ERROR"
        val source = intent.getStringExtra("source") ?: ""
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
            ckbs.add(ckb)
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
            finish()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.itemId
        return when (item.itemId) {
            R.id.action_cancel -> {finish(); true}
            R.id.action_delete -> {finish(); true}
            R.id.action_create -> {finish(); true}
            R.id.action_update -> {finish(); true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    // public boolean onOptionsItemSelected(MenuItem item) {
}