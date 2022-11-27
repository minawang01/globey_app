package APP_DESIGN_PROJECT.globeyapp

import APP_DESIGN_PROJECT.globeyapp.tools.Trips
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.content.Intent
import android.graphics.Color
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import com.captaindroid.tvg.Tvg

class MainActivity : AppCompatActivity() {

    private var button: Button? = null
    private lateinit var title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)
        title = findViewById(R.id.app_name)

        Tvg.change(title, Color.parseColor("#CA6884"),  Color.parseColor("#8AE9C1"));


        button!!.setOnClickListener {
            Log.e("GlobeyApp", "Add trip button was clicked")
            switchToTrips()
        }
    }

    private fun switchToTrips() {
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
                    val trip = Trips(map.get("name") as String, map.get("location") as String, map.get("start_date") as String, map.get("end_date") as String)
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
