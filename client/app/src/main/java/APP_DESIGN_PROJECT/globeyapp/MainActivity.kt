package APP_DESIGN_PROJECT.globeyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.content.Intent
import android.widget.ScrollView
import android.widget.SimpleAdapter
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var add_trip_btn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        populateTripData()

        add_trip_btn = findViewById(R.id.add_trip_btn)
        add_trip_btn!!.setOnClickListener {
            Log.e("GlobeyApp", "Add trip button was clicked")
            switchToAddTrip()
        }

    }

    private fun switchToAddTrip() {
        val intent = Intent(this, AddTripActivity::class.java)
        startActivity(intent)
    }

    private fun populateTripData() {
        var queue: RequestQueue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:5000/trips"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, {
                    response ->
                Log.e("GlobeyApp", "response was successful")

                val jsonArray:JSONArray = response.get("trips") as JSONArray
                val list = arrayListOf<MutableMap<String, Any>>()
                for (i in 0 until jsonArray.length()) {
                    val tripmap: JSONObject = jsonArray.get(i) as JSONObject
                    val map: MutableMap<String, Any> = HashMap()
                    map["Name"] = tripmap.get("name")
                    map["Location"] = tripmap.get("location")
                    map["Start"] = tripmap.get("start_date")
                    map["End"] = tripmap.get("end_date")
                    map["ID"] = tripmap.get("id")
                }

                val adapter = SimpleAdapter(this, list, R.layout.trip_list_item,
                arrayOf("Name", "Location", "Start", "End", "ID"), intArrayOf(R.id.name, R.id.location, R.id.start, R.id.end, R.id.index))
                val scroll_view: ScrollView = findViewById(R.id.scroll_view)
                scroll_view.adapter = adapter
            },
            { error ->
                Log.e("GlobeyApp", error.toString())
            }
        )
       queue.add(jsonObjectRequest)

    }
}