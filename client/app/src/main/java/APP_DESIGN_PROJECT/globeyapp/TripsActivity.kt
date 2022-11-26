package APP_DESIGN_PROJECT.globeyapp

import APP_DESIGN_PROJECT.globeyapp.tools.RecyclerViewAdapter
import APP_DESIGN_PROJECT.globeyapp.tools.Trips
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.os.Parcelable
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
import kotlin.collections.HashMap

class TripsActivity : AppCompatActivity(), RecyclerViewAdapter.ItemClickListener {
    private var add_trip_btn: ImageButton? = null
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var tripList: ArrayList<Trips>
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        tripList = intent.getParcelableArrayListExtra("trips")!!
        adapter = RecyclerViewAdapter(this, tripList)

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
        val trip: Trips = adapter.getItem(position)
        val intent = Intent(this, TripExpandedActivity::class.java)
        intent.putExtra("trip", trip)
        startActivity(intent)
    }

    override fun onDeleteBtnClick(view: View?, position: Int) {
        deleteTripFromDatabase(position)
    }

    private fun deleteTripFromDatabase(id: Int) {
        var queue: RequestQueue = Volley.newRequestQueue(this)
        val postData = JSONObject()
        try {
            postData.put("id", id)
        } catch(e : Exception) {
            Log.e("GlobeyApp", e.toString())
        }
        val url = "http://10.0.2.2:5000/delete_trips"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, postData, { _ ->
                Log.e("GlobeyApp", "response was successful")
                tripList.removeAt(id)
                adapter.notifyItemRemoved(id)
            },
            { error ->
                Log.e("GlobeyApp", error.toString())
            }
        )
        queue.add(jsonObjectRequest)
    }
}