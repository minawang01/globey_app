package APP_DESIGN_PROJECT.globeyapp

import APP_DESIGN_PROJECT.globeyapp.tools.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.collections.ArrayList


class TripExpandedActivity : AppCompatActivity(), NoteRecyclerViewAdapter.FocusChangeListener {
    private lateinit var adapter: NoteRecyclerViewAdapter
    private lateinit var edittext: EditText
    private lateinit var noteList: ArrayList<Notes>
    private lateinit var backBtn: ImageButton
    private lateinit var title: EditText
    private lateinit var location: EditText
    private lateinit var start: EditText
    private lateinit var end: EditText
    private lateinit var expandedTripImg: ImageButton
    private lateinit var toggle: Switch
    private var file_path: String? = null
    private var canEdit: Boolean = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_expanded_page)
        edittext = findViewById(R.id.add_note)

        location = findViewById(R.id.location_expanded)
        start = findViewById(R.id.start_expanded)
        end = findViewById(R.id.end_expanded)
        expandedTripImg = findViewById(R.id.trip_image_2)
        toggle = findViewById(R.id.edit_switch)
        backBtn = findViewById(R.id.back_btn)
        title = findViewById(R.id.trip_title2)




        noteList = intent.getParcelableArrayListExtra("notes", Notes::class.java)!!
        val trip = intent.getParcelableExtra("trip", Trips::class.java)!!

        adapter = NoteRecyclerViewAdapter(this, noteList)
        adapter.setFocusChangeListener(this)
        val recyclerView: RecyclerView = findViewById(R.id.notes_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        title.setText(trip.name)
        location.setText(trip.location)
        start.setText(trip.start)
        end.setText(trip.end)
        trip.file_path?.let { retrieveFromStorage(expandedTripImg, it) }

        edittext.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && canEdit) {
                val text = edittext.text.trim().toString()
                if(text.isNotEmpty()) {
                    addNoteToDatabase(trip.id, text)
                }
            }
        }

        location.setOnFocusChangeListener {_, hasFocus ->
            if (!hasFocus && canEdit) {
                changeTripInDatabase(trip.id, "LOCATION", location.text.toString())
            }

        }

        start.setOnFocusChangeListener {_, hasFocus ->
            if (!hasFocus && canEdit) {
                changeTripInDatabase(trip.id, "START_DATE", start.text.toString())
            }

        }

        end.setOnFocusChangeListener {_, hasFocus ->
            if (!hasFocus && canEdit) {
                changeTripInDatabase(trip.id, "END_DATE", end.text.toString())
            }
        }

        title.setOnFocusChangeListener{_, hasFocus ->
            if(!hasFocus && canEdit) {
                changeTripInDatabase(trip.id, "NAME", title.text.toString())
            }
        }

        expandedTripImg.setOnClickListener {
            if(canEdit) {
                changeImage(trip.id, expandedTripImg)
            }
        }

        backBtn.setOnClickListener {
            switchToTrips()
        }



        toggle.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            canEdit = isChecked
        }
    }

    private fun changeImage(id: Int, tripImg: ImageButton) {
        var launchSomeActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri = data?.data
                selectedImageUri?.let {
                    try {
                        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                        tripImg.setImageBitmap(bitmap)
                        file_path = saveToInternalStorage(bitmap, selectedImageUri.toString(), applicationContext)
                        changeTripInDatabase(id, "FILE_PATH", file_path.toString())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        imageChooser(launchSomeActivity)
    }

    private fun retrieveFromStorage(tripImg: ImageView, file_path: String) {
        try {
            val f = File(file_path)
            val b: Bitmap = BitmapFactory.decodeStream(FileInputStream(f))
            tripImg.setImageBitmap(b)
        } catch(e: FileNotFoundException) {
            Log.e("GlobeyApp", e.printStackTrace().toString())
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

    private fun changeTripInDatabase(trip_id: Int, element:String, text: String) {
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

    private fun switchToTrips() {
        var queue: RequestQueue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:5000/trips"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, {
                    response ->
                Log.e("GlobeyApp", "response was successful")

                val jsonArray: JSONArray = response.get("trips") as JSONArray
                var tripList = arrayListOf<Trips>()
                for (i in 0 until jsonArray.length()){
                    var map: JSONObject = jsonArray.get(i) as JSONObject
                    Log.e("tag", map.get("name") as String)
                    var file_path: String? = null
                    if(map.get("file_path") != "null") {
                        file_path = map.get("file_path").toString()
                    }
                    val trip = Trips(map.get("id") as Int, map.get("name") as String, map.get("location") as String,
                        map.get("start_date") as String, map.get("end_date") as String, file_path)
                    tripList.add(trip)
                }
                val intent = Intent(this, TripsActivity::class.java)
                val data: java.util.ArrayList<Trips> = tripList
                intent.putParcelableArrayListExtra("trips", data)
                startActivity(intent)
            },
            { error ->
                Log.e("GlobeyApp", error.toString())
            }
        )
        queue.add(jsonObjectRequest)

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