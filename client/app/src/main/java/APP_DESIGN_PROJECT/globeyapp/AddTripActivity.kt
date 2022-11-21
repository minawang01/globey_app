package APP_DESIGN_PROJECT.globeyapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class AddTripActivity: AppCompatActivity() {

    private var save_trip_btn: Button? = null
    private var delete_trip_btn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_expanded_page)
    }

    private fun sendMessage() {
        val url:String = "database url string"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, {
                response ->
                Log.e("GlobeyApp", "response was successful")
                switchActivity(response)
            },
            { error ->
                Log.e("GlobeyApp", error.toString())

            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun switchActivity(jsonObject: JSONObject) {
        // switch activity
    }


}