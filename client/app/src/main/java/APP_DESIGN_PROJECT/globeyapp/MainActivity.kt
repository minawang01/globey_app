package APP_DESIGN_PROJECT.globeyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.content.Intent

class MainActivity : AppCompatActivity() {

    private var add_trip_btn: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        add_trip_btn = findViewById(R.id.add_trip_btn)
        add_trip_btn!!.setOnClickListener {
            Log.e("MyActivity", "Add trip button was clicked")
            switchToAddTrip()
        }

    }

    private fun switchToAddTrip() {
        val intent = Intent(this, AddTripActivity::class.java)
        startActivity(intent)
    }
}