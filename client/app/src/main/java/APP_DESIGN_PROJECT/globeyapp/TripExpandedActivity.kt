package APP_DESIGN_PROJECT.globeyapp

import APP_DESIGN_PROJECT.globeyapp.tools.NoteRecyclerViewAdapter
import APP_DESIGN_PROJECT.globeyapp.tools.Notes
import APP_DESIGN_PROJECT.globeyapp.tools.RecyclerViewAdapter
import APP_DESIGN_PROJECT.globeyapp.tools.Trips
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import kotlin.properties.Delegates


class TripExpandedActivity : AppCompatActivity(), NoteRecyclerViewAdapter.FocusChangeListener {
    private lateinit var adapter: NoteRecyclerViewAdapter
    private lateinit var edittext: EditText
    private var id by Delegates.notNull<Int>()
    private lateinit var noteList: ArrayList<Notes>
    private lateinit var backBtn: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_expanded_page)
        edittext = findViewById(R.id.add_note)

        noteList = intent.getParcelableArrayListExtra("notes")!!
        val uri = intent.getStringExtra("uri")
        id = intent.getIntExtra("id", 0)
        Log.i("GlobeyApp", "$id")
        adapter = NoteRecyclerViewAdapter(this, noteList)
        adapter.setFocusChangeListener(this)

        val recyclerView: RecyclerView = findViewById(R.id.notes_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        edittext.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                addNoteToDatabase()
            }
        }

        backBtn = findViewById(R.id.back_btn)

        backBtn.setOnClickListener {
            val i = Intent(this, TripsActivity::class.java)
            startActivity(i)
        }
    }

    private fun addNoteToDatabase() {
        val postData = JSONObject()
        try {
            postData.put("text", edittext.text)
            postData.put("id", id)
        } catch(e :Exception) {
            Log.e("GlobeyApp", e.toString())
        }

        val url =  "http://10.0.2.2:5000/add_notes"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, postData, { response ->
                Log.e("GlobeyApp", "Post was successful")

                val json: JSONObject = response.get("notes") as JSONObject
                val note:Notes = Notes(json.get("id") as Int, json.get("timestamp") as String,
                    json.get("text") as String?
                )
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

    private fun changeNoteInDatabase(position: Int) {
        val note: Notes = adapter.getItem(position)
        val postData = JSONObject()
        try {
            postData.put("text", note.text)
            postData.put("id", note.id)
            postData.put("timestamp", note.timestamp)
        } catch(e :Exception) {
            Log.e("GlobeyApp", e.toString())
        }

        val url =  "http://10.0.2.2:5000/edit_notes"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, postData, { response ->
                Log.e("GlobeyApp", "Post was successful")

                val json: JSONObject = response.get("notes") as JSONObject
                val note:Notes = Notes(json.get("id") as Int, json.get("timestamp") as String,
                    json.get("text") as String?
                )
                noteList.removeAt(position)
                noteList.add(position, note)
                adapter.notifyItemChanged(position)
            },
            { error ->
                Log.e("GlobeyApp", error.toString())
            }
        )
        Volley.newRequestQueue(this).add(jsonObjectRequest)


    }

    override fun onFocusChange(view: View?, position: Int, hasFocus: Boolean) {
        if(!hasFocus) {
            changeNoteInDatabase(position)
        }
    }
}