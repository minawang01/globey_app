package APP_DESIGN_PROJECT.globeyapp

import APP_DESIGN_PROJECT.globeyapp.tools.NoteRecyclerViewAdapter
import APP_DESIGN_PROJECT.globeyapp.tools.Notes
import APP_DESIGN_PROJECT.globeyapp.tools.Trips
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import kotlin.collections.ArrayList


class TripExpandedActivity : AppCompatActivity(), NoteRecyclerViewAdapter.FocusChangeListener {
    private lateinit var adapter: NoteRecyclerViewAdapter
    private lateinit var edittext: EditText
    private lateinit var noteList: ArrayList<Notes>
    private lateinit var backBtn: ImageButton
    private lateinit var title: TextView
    private lateinit var location: EditText
    private lateinit var start: EditText
    private lateinit var end: EditText
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_expanded_page)
        edittext = findViewById(R.id.add_note)
        title = findViewById(R.id.trip_title)
        location = findViewById(R.id.location_expanded)
        start = findViewById(R.id.start_expanded)
        end = findViewById(R.id.end_expanded)

        noteList = intent.getParcelableArrayListExtra("notes", Notes::class.java)!!
        val trip = intent.getParcelableExtra("trip", Trips::class.java)!!
        val trip_id = trip.id

        adapter = NoteRecyclerViewAdapter(this, noteList)
        adapter.setFocusChangeListener(this)

        val recyclerView: RecyclerView = findViewById(R.id.notes_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        title.text = trip.name
        location.setText(trip.location)
        start.setText(trip.start)
        end.setText(trip.end)

        edittext.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = edittext.text.trim().toString()
                if(text.isNotEmpty()) {
                    addNoteToDatabase(trip_id, text)
                }
            }
        }

        location.setOnFocusChangeListener {_, hasFocus ->
            if (!hasFocus) {
                changeTripInDatabase(trip_id, "LOCATION", location.text)
            }

        }

        start.setOnFocusChangeListener {_, hasFocus ->
            if (!hasFocus) {
                changeTripInDatabase(trip_id, "START_DATE", start.text)
            }

        }

        end.setOnFocusChangeListener {_, hasFocus ->
            if (!hasFocus) {
                changeTripInDatabase(trip_id, "END_DATE", end.text)
            }
        }

        backBtn = findViewById(R.id.back_btn)

        backBtn.setOnClickListener {
            val i = Intent(this, TripsActivity::class.java)
            startActivity(i)
        }
    }

    private fun addNoteToDatabase(trip_id: Int, note: String) {
        val postData = JSONObject()
        try {
            postData.put("text", note)
            postData.put("id", trip_id)
        } catch(e :Exception) {
            Log.e("GlobeyApp", e.toString())
        }

        val url =  "http://10.0.2.2:5000/add_notes"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, postData, { response ->
                Log.e("GlobeyApp", "Post was successful")

                val json: JSONObject = response.get("notes") as JSONObject
                val note = Notes(json.get("id") as Int, json.get("trip_id") as Int, json.get("updated_time") as String,
                    json.get("note") as String)
                noteList.add(note)
                adapter.notifyItemInserted(noteList.size-1)
                edittext.setText("")
            },
            { error ->
                Log.e("GlobeyApp", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun changeNoteInDatabase(position: Int, text: String) {
        val note: Notes = adapter.getItem(position)

        val postData = JSONObject()
        try {
            postData.put("text", text)
            postData.put("note_id", note.id)
        } catch(e :Exception) {
            Log.e("GlobeyApp", e.toString())
        }

        val url =  "http://10.0.2.2:5000/edit_notes"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, postData, { response ->
                Log.e("GlobeyApp", "Post was successful")

                val json: JSONObject = response.get("notes") as JSONObject
                val note = Notes(json.get("id") as Int, json.get("trip_id") as Int, json.get("updated_time") as String,
                    json.get("note") as String
                )
                noteList[position] = note
                adapter.notifyItemChanged(position)
            },
            { error ->
                Log.e("GlobeyApp", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)


    }

    private fun deleteNoteInDatabase(position: Int) {
        val note: Notes = adapter.getItem(position)
        val postData = JSONObject()
        try {
            postData.put("note_id", note.id)
        } catch(e :Exception) {
            Log.e("GlobeyApp", e.toString())
        }

        val url =  "http://10.0.2.2:5000/delete_notes"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, postData, { _ ->
                Log.i("GlobeyApp", "Post was successful")
                noteList.removeAt(position)
                adapter.notifyItemRemoved(position)
            },
            { error ->
                Log.e("GlobeyApp", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun changeTripInDatabase(trip_id: Int, element:String, text: Editable) {
        val postData = JSONObject()
        try {
            postData.put("text", text.toString())
            postData.put("trip_id", trip_id)
            postData.put("element", element)
        } catch(e :Exception) {
            Log.e("GlobeyApp", e.toString())
        }

        val url =  "http://10.0.2.2:5000/edit_trip"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, postData, { _ ->
                Log.e("GlobeyApp", "Post was successful")
            },
            { error ->
                Log.e("GlobeyApp", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    override fun onFocusChange(view: View?, position: Int, hasFocus: Boolean, textField:EditText) {
        if(!hasFocus) {
            val text = textField.text.trim().toString()
            if(text.isEmpty()) {
                deleteNoteInDatabase(position)
            } else {
                changeNoteInDatabase(position, text)
            }
        }
    }
}