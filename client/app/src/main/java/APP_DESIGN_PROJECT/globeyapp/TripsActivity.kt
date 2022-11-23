package APP_DESIGN_PROJECT.globeyapp

import APP_DESIGN_PROJECT.globeyapp.tools.Trips
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.content.Intent
import android.widget.ImageButton
import android.widget.ListView
import android.widget.SimpleAdapter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class TripsActivity : AppCompatActivity() {
    private var add_trip_btn: ImageButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        val tripList = intent.getParcelableArrayListExtra<Trips>("trips")
        val list = arrayListOf<MutableMap<String, String>>()
        for(trip in tripList!!) {
            val map: MutableMap<String, String> = HashMap()
            map["Name"] = trip.name.toString()
            map["Location"] = trip.location.toString()
            map["Countdown"] = "${getCountdown(trip!!.start!!)} day(s)"
            list.add(map)

        }

        val adapter = SimpleAdapter(this, list, R.layout.trip_list_item,
            arrayOf("Name", "Location", "Countdown"), intArrayOf(R.id.trip_name, R.id.trip_location, R.id.count_dwn))
        val list_view: ListView = findViewById(R.id.trip_view)
        list_view.adapter = adapter

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
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    }
}