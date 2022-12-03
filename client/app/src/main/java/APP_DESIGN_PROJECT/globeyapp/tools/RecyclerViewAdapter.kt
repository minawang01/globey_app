package APP_DESIGN_PROJECT.globeyapp.tools

import APP_DESIGN_PROJECT.globeyapp.R
import APP_DESIGN_PROJECT.globeyapp.TripsActivity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class RecyclerViewAdapter(context: Context?, data: List<Trips>, cr: ContentResolver):
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private val mData: List<Trips>
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null
    private val contentResolver: ContentResolver

    init {
        mInflater = LayoutInflater.from(context)
        mData = data
        contentResolver = cr
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.trip_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trip = mData[position]
        holder.location.text = trip.location
        holder.name.text = trip.name
        trip.file_path?.let { retrieveFromStorage(holder.imgBtn, it) }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    private fun retrieveFromStorage(imgBtn: ImageButton, file_path: String) {
        try {
            val f = File(file_path)
            val b: Bitmap = BitmapFactory.decodeStream(FileInputStream(f))
            imgBtn.setImageBitmap(b)
        } catch(e:FileNotFoundException) {
            Log.e("GlobeyApp", e.printStackTrace().toString())
        }
    }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            var location: TextView
            var name: TextView
            var countdown: TextView
            var delete: Button
            var imgBtn: ImageButton

            init {
                name = itemView.findViewById(R.id.trip_name)
                location = itemView.findViewById(R.id.trip_location)
                countdown = itemView.findViewById(R.id.count_dwn)
                delete = itemView.findViewById(R.id.delete_btn)
                imgBtn = itemView.findViewById(R.id.imageButton)
                itemView.setOnClickListener(this)
                delete.setOnClickListener(this)
                imgBtn.setOnClickListener(this)
            }

            override fun onClick(view: View) {
                if (view.id == delete.id) {
                    if (mClickListener != null) mClickListener!!.onDeleteBtnClick(
                        view,
                        bindingAdapterPosition
                    )
                } else if(view.id == imgBtn.id) {
                    if (mClickListener != null) mClickListener!!.onImgBtnClick(
                        view,
                        bindingAdapterPosition
                    )

                } else {
                    if (mClickListener != null) mClickListener!!.onItemClick(
                        view,
                        bindingAdapterPosition
                    )
                }
            }
        }

        fun getItem(id: Int): Trips {
            return mData[id]
        }

        fun setClickListener(itemClickListener: ItemClickListener?) {
            mClickListener = itemClickListener
        }

        interface ItemClickListener {
            fun onItemClick(view: View?, position: Int)
            fun onDeleteBtnClick(view: View?, position: Int)
            fun onImgBtnClick(view: View?, position: Int)
        }
    }