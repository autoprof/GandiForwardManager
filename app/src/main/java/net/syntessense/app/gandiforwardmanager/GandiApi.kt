package net.syntessense.app.gandiforwardmanager

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

abstract class GandiApi(var apiKey: String, ctx : Context) {
    private val queue = Volley.newRequestQueue(ctx)

    open fun notifyError(msg: String) {
        notify("Error : $msg")
    }
    open fun notify(msg: String) {}
    open fun onPutReady() {}
    open fun onCreateReady() {}
    open fun onDeleteQuery() {}
    open fun onDeleteReady() {}
    open fun onDomainsQuery() {}
    open fun onDomainsReady(domains: ArrayList<Domain>) {}
    open fun onAddressesQuery() {}
    open fun onAddressesReady(addresses: ArrayList<Address>) {}

    open fun getJSON(source: String, destinations: ArrayList<String>): String {
        val dests = JSONArray()
        for (d in destinations)
            dests.put(d)
        val obj = JSONObject()
        obj.put("source", source)
        obj.put("destinations", dests)
        return obj.toString()
    }

    open fun createAddress(domain: String, source: String, destinations: ArrayList<String>) {
        try {
            val json = getJSON(source, destinations)
            queryBody(
                Request.Method.POST,
                "https://api.gandi.net/v5/email/forwards/$domain",
                json
            ) { onCreateReady() }
        } catch (e: Exception) {
            e.message?.let { notifyError(it) }
        }
    }

    open fun updateAddress(domain: String, source: String, destinations: ArrayList<String>) {
        try {
            val json = getJSON(source, destinations)
            queryBody(
                Request.Method.PUT,
                "https://api.gandi.net/v5/email/forwards/$domain/$source",
                json
            ) { onPutReady() }
        } catch (e: java.lang.Exception) {
            e.message?.let { notifyError(it) }
        }
    }

    open fun deleteAddress(domain: String, source: String) {
        onDeleteQuery()
        query(
            Request.Method.DELETE,
            "https://api.gandi.net/v5/email/forwards/$domain/$source"
        ) { onDeleteReady() }
    }

    open fun getAddresses(domain: String) {
        onAddressesQuery()
        query(
            Request.Method.GET,
            "https://api.gandi.net/v5/email/forwards/$domain"
        ) { response ->
            try {
                onAddressesReady(Address.parseAddresses(response))
            } catch (e: JSONException) {
                notifyError(e.toString())
            }
        }
    }

    open fun getDomains() {
        onDomainsQuery()
        query(
            Request.Method.GET,
            "https://api.gandi.net/v5/domain/domains"
        ) { response ->
            try {
                onDomainsReady(Domain.parseDomains(apiKey, response))
            } catch (e: JSONException) {
                notifyError(e.toString())
            }
        }
    }

    open fun query(method: Int, url: String, callback: Response.Listener<String>) {
        queue.add(APIRequest(
            apiKey,
            method,
            url,
            callback,
            {notifyError(it.toString())}
        ))
    }

    open fun queryBody(method: Int, url: String, body: String, callback: Response.Listener<String>) {
        queue.add(APIRequest(
            apiKey,
            method,
            url,
            body,
            callback,
            {notifyError(it.toString())}
        ))
    }

    class APIRequest : StringRequest {

        private val headers: MutableMap<String, String>
        private var body: ByteArray

        constructor(
            apiKey: String,
            method: Int,
            url: String,
            listener: Response.Listener<String>?,
            errorListener: Response.ErrorListener?
        ) : super(method, url, listener, errorListener) {
            headers = HashMap()
            headers["Authorization"] = "Apikey $apiKey"
            headers["Content-Type"] = "application/json"
            headers["Accept"] = "application/json"
            body = "".toByteArray()
        }

        constructor(
            apiKey: String,
            method: Int,
            url: String,
            content: String,
            listener: Response.Listener<String>?,
            errorListener: Response.ErrorListener?
        ) : super(method, url, listener, errorListener) {
            headers = HashMap()
            headers["Authorization"] = "Apikey $apiKey"
            headers["Content-Type"] = "application/json"
            headers["Accept"] = "application/json"
            body = content.toByteArray()
        }

        override fun getHeaders(): Map<String, String> {
            return headers
        }

        override fun getBody(): ByteArray {
            return body
        }
    }

}
