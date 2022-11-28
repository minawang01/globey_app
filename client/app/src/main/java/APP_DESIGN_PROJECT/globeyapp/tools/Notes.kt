package APP_DESIGN_PROJECT.globeyapp.tools

import android.os.Parcel
import android.os.Parcelable

class Notes(var id: Int, var timestamp: String?, var text: String?): Parcelable {

    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        timestamp = parcel.readString(),
        text = parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest?.writeInt(id)
        dest?.writeString(timestamp)
        dest?.writeString(text)
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