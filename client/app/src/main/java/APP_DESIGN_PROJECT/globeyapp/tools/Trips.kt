package APP_DESIGN_PROJECT.globeyapp.tools

import android.os.Parcel
import android.os.Parcelable

class Trips(val id: Int, val name: String?, val location: String?, val start: String?,
            val end:String?, val uri: String?) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        name = parcel.readString(),
        location = parcel.readString(),
        start = parcel.readString(),
        end = parcel.readString(),
        uri = parcel.readString(),
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0.writeInt(id)
        p0.writeString(name)
        p0.writeString(location)
        p0.writeString(start)
        p0.writeString(end)
        p0.writeString(uri)
    }

    companion object CREATOR : Parcelable.Creator<Trips> {
        override fun createFromParcel(parcel: Parcel): Trips {
            return Trips(parcel)
        }

        override fun newArray(size: Int): Array<Trips?> {
            return arrayOfNulls(size)
        }
    }

}
