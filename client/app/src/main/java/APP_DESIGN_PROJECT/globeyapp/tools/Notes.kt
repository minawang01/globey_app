package APP_DESIGN_PROJECT.globeyapp.tools

import android.os.Parcel
import android.os.Parcelable

class Notes(val id: Int, val trip_id: Int, var updated_time: String?, var note: String?): Parcelable {

    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        trip_id = parcel.readInt(),
        updated_time = parcel.readString(),
        note = parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest?.writeInt(id)
        dest?.writeInt(trip_id)
        dest?.writeString(updated_time)
        dest?.writeString(note)
    }


    companion object CREATOR : Parcelable.Creator<Notes> {
        override fun createFromParcel(parcel: Parcel): Notes {
            return Notes(parcel)
        }

        override fun newArray(size: Int): Array<Notes?> {
            return arrayOfNulls(size)
        }
    }

}