package net.syntessense.app.gandiforwardmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference

class SettingsActivity : AppCompatActivity() {

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, SettingsFragment())
                    .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

            val domains = ArrayList<String>()
            domains.add("domain1")
            domains.add("domain2")
            domains.add("domain3")

            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefCat = PreferenceCategory(preferenceScreen.context)
            prefCat.title = "Domains to show in list"
            preferenceScreen.addPreference(prefCat)
            var domainPref : SwitchPreference
            for (d in domains) {
                domainPref = SwitchPreference(preferenceScreen.context)
                domainPref.title = d
                domainPref.key = "show_domain_$d"
                prefCat.addPreference(domainPref)
            }

        }
    }
}