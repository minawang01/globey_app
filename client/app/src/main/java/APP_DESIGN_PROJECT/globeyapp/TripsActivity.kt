package APP_DESIGN_PROJECT.globeyapp

import APP_DESIGN_PROJECT.globeyapp.tools.Notes
import APP_DESIGN_PROJECT.globeyapp.tools.RecyclerViewAdapter
import APP_DESIGN_PROJECT.globeyapp.tools.Trips
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class TripsActivity : AppCompatActivity(), RecyclerViewAdapter.ItemClickListener {
    private var add_trip_btn: ImageButton? = null
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var tripList: ArrayList<Trips>
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        tripList = intent.getParcelableArrayListExtra("trips")!!
        adapter = RecyclerViewAdapter(this, tripList, this.contentResolver)

        val recyclerView: RecyclerView = findViewById(R.id.trip_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.setClickListener(this)
        recyclerView.adapter = adapter

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

    private fun getCountdown(date:String):Long {
        val dateArray = date.split("/")
        val startDate = Date(dateArray[2].toInt(), dateArray[1].toInt(), dateArray[0].toInt())
        val startDateMillis = startDate.time
        val cal = Calendar.getInstance()
        val currentDate = cal.timeInMillis
        val diff:Long = startDateMillis - currentDate
        return TimeUnit.MILLISECONDS.toDays(diff);
    }

    override fun onItemClick(view: View?, position: Int) {
        getNotesFromDatabase(position)
    }

    override fun onDeleteBtnClick(view: View?, position: Int) {
        deleteTripFromDatabase(position)
    }

    override fun onImgBtnClick(view: View?, position: Int) {
        getNotesFromDatabase(position)
    }

    private fun deleteTripFromDatabase(position: Int) {
        var queue: RequestQueue = Volley.newRequestQueue(this)
        val trip: Trips = adapter.getItem(position)
        val postData = JSONObject()
        try {
            postData.put("id", trip.id)
        } catch(e : Exception) {
            Log.e("GlobeyApp", e.toString())
        }
        val url = "http://10.0.2.2:5000/delete_trips"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, postData, { _ ->
                Log.e("GlobeyApp", "response was successful")
                tripList.removeAt(position)
                adapter.notifyItemRemoved(position)
            },
            { error ->
                Log.e("GlobeyApp", error.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }

    private fun getNotesFromDatabase(position: Int) {
        var queue: RequestQueue = Volley.newRequestQueue(this)
        val trip: Trips = adapter.getItem(position)
        val postData = JSONObject()
        try {
            postData.put("id", trip.id)
        } catch(e :Exception) {
            Log.e("GlobeyApp", e.toString())
        }
        var url = "http://10.0.2.2:5000/notes"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, postData, {
            response ->
            Log.i("GlobeyApp", "response was successful")
            val jsonArray:JSONArray = response.get("notes") as JSONArray

            var noteList = arrayListOf<Notes>()
            for (i in 0 until jsonArray.length()){
                var map: JSONObject = jsonArray.get(i) as JSONObject
                val note = Notes(map.get("id") as Int, map.get("trip_id") as Int, map.get("updated_time") as String,
                    map.get("note") as String

                )
                noteList.add(note)
            }
            val intent = Intent(this, TripExpandedActivity::class.java)
            val data: ArrayList<Notes> = noteList
            intent.putParcelableArrayListExtra("notes", data)
            intent.putExtra("uri", trip.uri)
            intent.putExtra("id", trip.id)
            intent.putExtra("trip_name", trip.name)
            startActivity(intent)
        }, { error ->
                Log.e("GlobeyApp", error.toString())
            })
        queue.add(jsonObjectRequest)
    }
}