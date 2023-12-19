import android.os.Parcel
import android.os.Parcelable

class PenyewaModel2(
    var no: String,
    var id: String,
    var userid: String,
    var nama: String,
    var tgllahir: String,
    var alamat: String,
    var telepon: String,
    var email: String,
    var nokamar: String,
    var gambar:String

) : Parcelable {

    // Parcelable constructor
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
        )

    // Write to parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(no)
        parcel.writeString(id)
        parcel.writeString(userid)
        parcel.writeString(nama)
        parcel.writeString(tgllahir)
        parcel.writeString(alamat)
        parcel.writeString(telepon)
        parcel.writeString(email)
        parcel.writeString(nokamar)
        parcel.writeString(gambar)
    }

    // Describe contents (always return 0)
    override fun describeContents(): Int {
        return 0
    }

    // Companion object to create instances of Parcelable class from a Parcel
    companion object CREATOR : Parcelable.Creator<PenyewaModel> {
        override fun createFromParcel(parcel: Parcel): PenyewaModel {
            return PenyewaModel(parcel)
        }

        override fun newArray(size: Int): Array<PenyewaModel?> {
            return arrayOfNulls(size)
        }
    }
}


