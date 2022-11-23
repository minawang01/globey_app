package APP_DESIGN_PROJECT.globeyapp.tools

import android.os.Parcel
import android.os.Parcelable

class Trips(val name: String?, val location: String?, val start: String?, private val end:String?) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        name = parcel.readString(),
        location = parcel.readString(),
        start = parcel.readString(),
        end = parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeString(name)
        p0?.writeString(location)
        p0?.writeString(start)
        p0?.writeString(end)
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
