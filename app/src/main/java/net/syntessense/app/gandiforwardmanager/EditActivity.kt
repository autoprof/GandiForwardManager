package net.syntessense.app.gandiforwardmanager

import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import net.syntessense.app.gandiforwardmanager.databinding.EditActivityBinding

class EditActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: EditActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var mainForwards = ArrayList<Target>()
        mainForwards.add(Target("tgt1", true))
        mainForwards.add(Target("tgt2", true))
        mainForwards.add(Target("tgt3", false))
        mainForwards.add(Target("tgt4", false))

        binding = EditActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);

        var isNew = intent.getBooleanExtra("new", false);
        var domain = intent.getStringExtra("domain") ?: "ERROR";
        var source = intent.getStringExtra("source") ?: "";
        var destinations = intent.getStringArrayListExtra("destinations") ?: ArrayList();

        if ( !isNew ) {
            title = "$source@$domain"
        } else {
            title = "<new>@$domain"
        }

        findViewById<TextView>(R.id.editDomain).text = domain
        findViewById<TextView>(R.id.editSource).text = source
        var editFields = findViewById<LinearLayout>(R.id.editFields);

        var ckb : CheckBox
        var ckbs = ArrayList<CheckBox>()
        for (i in mainForwards) {
            ckb = CheckBox(this)
            ckb.text = i.address
            ckb.isChecked = (isNew && i.selected) || destinations.contains(i.address)
            editFields.addView(ckb)
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
        findViewById<EditText>(R.id.editTarget).setText(secondaryForwards)

        binding.fab.setOnClickListener { view ->
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}