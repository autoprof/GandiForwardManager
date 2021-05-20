package net.syntessense.app.gandiforwardmanager

import android.content.Context
import com.android.volley.toolbox.Volley

abstract class GandiApi(ctx : Context) {

    val requestQueue = Volley.newRequestQueue(ctx)

}
