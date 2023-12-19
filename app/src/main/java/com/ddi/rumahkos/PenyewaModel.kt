import android.os.Parcel
import android.os.Parcelable

class PenyewaModel(
    var no: String,
    var id: String,
    var userid: String,
    var tanggal: String,
    var judul: String,
    var tanggallahir: String,
    var nama:String,
    var nokamar:String,
    var gambar:String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: "",
        parcel.readString()?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(no)
        parcel.writeString(id)
        parcel.writeString(userid)
        parcel.writeString(tanggal)
        parcel.writeString(judul)
        parcel.writeString(tanggallahir)
        parcel.writeString(nama)
        parcel.writeString(nokamar)
        parcel.writeString(gambar)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PenyewaModel> {
        override fun createFromParcel(parcel: Parcel): PenyewaModel {
            return PenyewaModel(parcel)
        }

        override fun newArray(size: Int): Array<PenyewaModel?> {
            return arrayOfNulls(size)
        }
    }


}


