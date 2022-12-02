package APP_DESIGN_PROJECT.globeyapp

import APP_DESIGN_PROJECT.globeyapp.tools.NoteRecyclerViewAdapter
import APP_DESIGN_PROJECT.globeyapp.tools.Notes
import android.content.Intent
import android.icu.util.VersionInfo
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


class TripExpandedActivity : AppCompatActivity(), NoteRecyclerViewAdapter.FocusChangeListener {
    private lateinit var adapter: NoteRecyclerViewAdapter
    private lateinit var edittext: EditText
    private var trip_id by Delegates.notNull<Int>()
    private lateinit var noteList: ArrayList<Notes>
    private lateinit var backBtn: ImageButton
    private lateinit var title: TextView
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_expanded_page)
        edittext = findViewById(R.id.add_note)
        title = findViewById(R.id.trip_title)

        noteList = intent.getParcelableArrayListExtra("notes", Notes::class.java)!!
        val uri = intent.getStringExtra("uri")
        trip_id = intent.getIntExtra("id", 0)
        adapter = NoteRecyclerViewAdapter(this, noteList)
        adapter.setFocusChangeListener(this)

        val recyclerView: RecyclerView = findViewById(R.id.notes_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        title.text = intent.getStringExtra("trip_name")

        edittext.setOnFocusChangeListener { _, hasFocus ->
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

    private fun changeNoteInDatabase(position: Int, textField: EditText) {
        val note: Notes = adapter.getItem(position)

        Log.i("GlobeyApp", "the note says this ${textField.text}")
        val postData = JSONObject()
        try {
            postData.put("text", textField.text)
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

    override fun onFocusChange(view: View?, position: Int, hasFocus: Boolean, text:EditText) {
        if(!hasFocus) {
            changeNoteInDatabase(position, text)
        }
    }
}