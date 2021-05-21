package net.syntessense.app.gandiforwardmanager

import org.json.JSONArray
import org.json.JSONObject

class Target(var address : String, var selected : Boolean) {
    companion object {
        fun parseList(str : String) : ArrayList<Target> {
            val mainTargets = ArrayList<Target>()
            var tgt : List<String>
            for ( mf in str.split("\n") ) {
                if (mf.trim().isNotEmpty()) {
                    tgt = mf.split(":")
                    mainTargets.add(
                        Target(
                            tgt[0],
                            tgt.size > 1 && tgt[1] == "1"
                        )
                    )
                }
            }
            return mainTargets
        }
    }
}

class Domain (var fqdn: String, var apiKey: String) {
    companion object {
        fun parseDomains(apiKey: String, json: String): ArrayList<Domain> {
            val domains: ArrayList<Domain> = ArrayList()
            val reader = JSONArray(json)
            val l = reader.length()
            var obj: JSONObject
            for (i in 0 until l) {
                obj = reader.getJSONObject(i)
                domains.add(Domain(obj.getString("fqdn"), apiKey))
            }
            return domains
        }
    }
}

class Address (var source: String, var destinations : ArrayList<String>) {
    companion object {
        fun parseAddresses(json: String): ArrayList<Address> {
            val addresses: ArrayList<Address> = ArrayList()
            val reader = JSONArray(json)
            val l = reader.length()
            var obj: JSONObject
            var to: JSONArray
            var tos: ArrayList<String>
            for (i in 0 until l) {
                obj = reader.getJSONObject(i)
                to = obj.getJSONArray("destinations")
                tos = ArrayList()
                for (j in 0 until to.length()) tos.add(to.getString(j))
                addresses.add(Address(obj.getString("source"), tos))
            }
            return addresses
        }
    }
}