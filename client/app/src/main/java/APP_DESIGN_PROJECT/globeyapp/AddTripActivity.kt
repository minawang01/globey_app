package APP_DESIGN_PROJECT.globeyapp

import APP_DESIGN_PROJECT.globeyapp.tools.Trips
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList


class AddTripActivity: AppCompatActivity() {

    private var confirm_btn: ImageButton? = null
    private var discard_btn: ImageButton? = null
    private var trip_name: EditText? = null
    private var trip_location: EditText? = null
    private var start_date: EditText? = null
    private var end_date: EditText? = null
    private var trip_img: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trip_page)
        confirm_btn = findViewById(R.id.confirm_btn)
        discard_btn = findViewById(R.id.discard_btn)

        confirm_btn!!.setOnClickListener {
            trip_name = findViewById(R.id.trip_name)
            trip_location = findViewById(R.id.location)
            start_date = findViewById(R.id.start_date)
            end_date = findViewById(R.id.end_date)
            trip_img = findViewById(R.id.add_trip_img_btn)

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

        discard_btn!!.setOnClickListener {
            Log.e("GlobeyApp", "Trip was not saved")
            switchActivity()
        }
    }

    private fun sendMessage(trip: JSONObject) {
        val url =  "http://10.0.2.2:5000/trips"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, trip, { response ->
                Log.e("GlobeyApp", "Post was successful")

                val jsonArray: JSONArray = response.get("trips") as JSONArray
                var tripList = arrayListOf<Trips>()
                for (i in 0 until jsonArray.length()){
                    var map: JSONObject = jsonArray.get(i) as JSONObject
                    Log.e("tag", map.get("name") as String)
                    val trip = Trips(map.get("id") as Int, map.get("name") as String, map.get("location") as String, map.get("start_date") as String, map.get("end_date") as String)
                    tripList.add(trip)
                }
                val intent = Intent(this, TripsActivity::class.java)
                val data: ArrayList<Trips> = tripList
                intent.putParcelableArrayListExtra("trips", data)
                startActivity(intent)
            },
            { error ->
                Log.e("GlobeyApp", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun switchActivity() {
        var queue: RequestQueue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:5000/trips"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, {
                    response ->
                Log.e("GlobeyApp", "response was successful")

                val jsonArray:JSONArray = response.get("trips") as JSONArray
                var tripList = arrayListOf<Trips>()
                for (i in 0 until jsonArray.length()){
                    var map: JSONObject = jsonArray.get(i) as JSONObject
                    Log.e("tag", map.get("name") as String)
                    val trip = Trips(map.get("id") as Int, map.get("name") as String, map.get("location") as String, map.get("start") as String, map.get("end") as String)
                    tripList.add(trip)
                }
                val intent = Intent(this, TripsActivity::class.java)
                val data: ArrayList<Trips> = tripList
                intent.putParcelableArrayListExtra("trips", data)
                startActivity(intent)
            },
            { error ->
                Log.e("GlobeyApp", error.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }
}
