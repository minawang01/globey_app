package APP_DESIGN_PROJECT.globeyapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class AddTripActivity: AppCompatActivity() {

    private var save_trip_btn: Button? = null
    private var dont_save_btn: Button? = null
    private var trip_name: EditText? = null
    private var trip_location: EditText? = null
    private var start_date: EditText? = null
    private var end_date: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_expanded_page)

        save_trip_btn!!.setOnClickListener {
            trip_name = findViewById(R.id.trip_name)
            trip_location = findViewById(R.id.location)
            start_date = findViewById(R.id.start_date)
            end_date = findViewById(R.id.end_date)

            val name: String = trip_name!!.text.toString()
            val location: String = trip_location!!.text.toString()
            val start: String = start_date!!.text.toString()
            val end: String = end_date!!.text.toString()

            val postData = JSONObject()
            try {
                postData.put("name", name)
                postData.put("location", location)
                postData.put("start", start)
                postData.put("end", end)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            sendMessage(postData)
        }

        dont_save_btn!!.setOnClickListener {
            Log.e("GlobeyApp", "Trip was not saved")
            switchActivity()
        }
    }

    private fun sendMessage(trip: JSONObject) {
        var queue: RequestQueue = Volley.newRequestQueue(this)
        val url:String =  "http://10.0.2.2:5000/trips"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, trip, {
                response ->
                Log.e("GlobeyApp", "Post was successful")
                switchActivity()
            },
            { error ->
                Log.e("GlobeyApp", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun switchActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
