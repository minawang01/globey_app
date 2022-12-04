package APP_DESIGN_PROJECT.globeyapp.tools

import APP_DESIGN_PROJECT.globeyapp.R
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RecyclerViewAdapter(context: Context, data: List<Trips>, cr: ContentResolver):
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private val mData: List<Trips>
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null
    private val contentResolver: ContentResolver
    private val mContext: Context

    init {
        mInflater = LayoutInflater.from(context)
        mData = data
        contentResolver = cr
        mContext = context
    }

    companion object {
        val DEFAULT_LOCATION = "Location"
        val DEFAULT_NAME = "Untitled Trip"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.trip_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trip = mData[position]
        holder.location.text = trip.location ?: DEFAULT_LOCATION
        holder.name.text = trip.name ?: DEFAULT_NAME
        if (trip.file_path.isNullOrBlank()) {
            setDefaultImage(holder.imgBtn, mContext, this.contentResolver)
        } else {
            retrieveFromStorage(holder.imgBtn, trip.file_path)
        }
        holder.countdown.text = trip.start?.let { "${getCountdown(it)} days" }
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
            setDefaultImage(imgBtn, mContext, this.contentResolver)
        }
    }

    private fun getCountdown(date:String):Long {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val startDate = sdf.parse(date)
        val startDateMillis = startDate.time
        Log.i("GlobeyApp", "Start $startDateMillis")

        val cal = Calendar.getInstance()
        val currentDate = cal.timeInMillis
        Log.i("GlobeyApp", "Current $currentDate")
        val diff:Long = startDateMillis - currentDate
        Log.i("GlobeyApp", "diff $diff")
        return TimeUnit.MILLISECONDS.toDays(diff);
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
                when(view.id) {
                    delete.id -> mClickListener?.onDeleteBtnClick(view, bindingAdapterPosition)
                    imgBtn.id -> mClickListener?.onImgBtnClick(view, bindingAdapterPosition)
                    else -> mClickListener?.onItemClick(view, bindingAdapterPosition)
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