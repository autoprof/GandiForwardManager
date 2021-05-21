package net.syntessense.app.gandiforwardmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import com.google.android.material.snackbar.Snackbar

class SettingsActivity : AppCompatActivity() {

    private var ctx = this
    private lateinit var fragment : SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            fragment = SettingsFragment()
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, fragment)
                    .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getApi().getDomains()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    fun getApi(): GandiApi {
        return object: GandiApi(ctx) {
            override fun onDomainsReady(domains: ArrayList<Domain>) {
                val prefCat = PreferenceCategory(fragment.preferenceScreen.context)
                prefCat.title = "Domains to show in list"
                fragment.preferenceScreen.addPreference(prefCat)
                var domainPref : SwitchPreference
                for (d in domains) {
                    domainPref = SwitchPreference(fragment.preferenceScreen.context)
                    domainPref.title = d.fqdn
                    domainPref.key = "show_domain_${d.fqdn}"
                    prefCat.addPreference(domainPref)
                }
            }
        }
    }

}