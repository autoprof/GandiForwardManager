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
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import net.syntessense.app.gandiforwardmanager.databinding.EditActivityBinding

class EditActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: EditActivityBinding
    private var ckbs = ArrayList<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        var mfs = PreferenceManager.getDefaultSharedPreferences(this).getString("faddresses", "") ?: ""
        var mainForwards = ArrayList<Target>()
        for ( mf in mfs.split("\n") ) {
            var mflr = mf.split(":")
            mainForwards.add(
                net.syntessense.app.gandiforwardmanager.Target(
                    mflr[0],
                    mflr.size > 1 && mflr[1] == "1"
                )
            )
        }


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
        findViewById<EditText>(R.id.editSource).setText(source)
        var editFields = findViewById<LinearLayout>(R.id.editFields);

        var ckb : CheckBox
        for (i in mainForwards) {
            ckb = CheckBox(this)
            ckb.text = i.address
            ckb.isChecked = (isNew && i.selected) || destinations.contains(i.address)
            editFields.addView(ckb)
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