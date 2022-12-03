package APP_DESIGN_PROJECT.globeyapp

import APP_DESIGN_PROJECT.globeyapp.tools.Trips
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList


class AddTripActivity: AppCompatActivity() {


    private var confirm_btn: ImageButton? = null
    private var discard_btn: ImageButton? = null
    private var trip_name: EditText? = null
    private var trip_location: EditText? = null
    private var start_date: EditText? = null
    private var end_date: EditText? = null
    private var trip_img: ImageButton? = null
    private var file_path: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trip_page)
        confirm_btn = findViewById(R.id.confirm_btn)
        discard_btn = findViewById(R.id.discard_btn)
        trip_img = findViewById(R.id.add_trip_img_btn)

        trip_img?.bringToFront()
        trip_img?.setOnClickListener {
            imageChooser()
        }
        trip_img?.scaleType = ImageView.ScaleType.CENTER

        confirm_btn!!.setOnClickListener {
            trip_name = findViewById(R.id.trip_name)
            trip_location = findViewById(R.id.location)
            start_date = findViewById(R.id.start_date)
            end_date = findViewById(R.id.end_date)


            val name: String = trip_name!!.text.toString()
            val location: String = trip_location!!.text.toString()
            val start: String = start_date!!.text.toString()
            val end: String = end_date!!.text.toString()

            val postData = JSONObject()
            try {
                postData.put("name", name)
                postData.put("location", location)
                postData.put("start", start)
                postData.put("end", end)
                postData.put("file_path", file_path)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            sendMessage(postData)
        }

        discard_btn!!.setOnClickListener {
            Log.i("GlobeyApp", "Trip was not saved")
            switchActivity()
        }
    }

    private fun sendMessage(trip: JSONObject) {
        val url = "http://10.0.2.2:5000/trips"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, trip, { response ->
                Log.e("GlobeyApp", "Post was successful")

                val jsonArray: JSONArray = response.get("trips") as JSONArray
                var tripList = arrayListOf<Trips>()

                for (i in 0 until jsonArray.length()) {
                    var map: JSONObject = jsonArray.get(i) as JSONObject
                    Log.e("tag", map.get("name") as String)
                    var file_path: String? = null
                    if (map.get("file_path") != "null") {
                        file_path = map.get("file_path").toString()
                    }
                    val trip = Trips(
                        map.get("id") as Int, map.get("name") as String,
                        map.get("location") as String, map.get("start_date") as String,
                        map.get("end_date") as String, file_path
                    )
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
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private fun switchActivity() {
        var queue: RequestQueue = Volley.newRequestQueue(this)
        val url = "http://10.0.2.2:5000/trips"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null, { response ->
                Log.e("GlobeyApp", "response was successful")

                val jsonArray: JSONArray = response.get("trips") as JSONArray
                var tripList = arrayListOf<Trips>()
                for (i in 0 until jsonArray.length()) {
                    var map: JSONObject = jsonArray.get(i) as JSONObject
                    Log.e("tag", map.get("name") as String)
                    var uri: String? = null
                    if (map.get("img_uri") != "null") {
                        uri = map.get("img_uri").toString()
                    }
                    val trip = Trips(
                        map.get("id") as Int,
                        map.get("name") as String,
                        map.get("location") as String,
                        map.get("start_date") as String,
                        map.get("end_date") as String,
                        uri
                    )
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

    private fun imageChooser() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        launchSomeActivity.launch(i)
    }

    private var launchSomeActivity = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            if (data != null && data.data != null) {
                val selectedImageUri: Uri? = data.data
                val selectedImageBitmap: Bitmap
                try {
                    selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                    trip_img?.setImageBitmap(selectedImageBitmap)
                    file_path = saveToInternalStorage(selectedImageBitmap, selectedImageUri.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun createFileName(uri:String):String {
        val strings = uri.split("%")
        return strings[strings.size - 1]
    }

    private fun saveToInternalStorage(bitmap:Bitmap, uri:String):String {
        val root_dir: File = ContextWrapper(applicationContext).getDir("imageDir", Context.MODE_PRIVATE)
        val fp = File(root_dir,createFileName(uri))
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(fp)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        } catch (e: Exception) {
            Log.e("GlobeyApp", e.printStackTrace().toString())

        } finally {
            try {
                fos?.close()
            } catch (e:IOException) {
                Log.e("GlobeyApp", e.printStackTrace().toString())
            }
        }
        return fp.toString()
    }

}

