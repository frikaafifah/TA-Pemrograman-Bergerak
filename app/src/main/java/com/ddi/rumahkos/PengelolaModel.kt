import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable


class PengelolaModel(
    var no: String,
    var id: String,
    var userid: String,
    var nama: String,
    var password: String,
    var tanggallahir: String,
    var email: String,
    var alamat: String,
    var nokamar: String,
    var sewa: String,
    var telepon: String,
    var kontakdarurat: String,
    var tanggalmasuk: String,
    var tanggalkeluar: String,
    var statusbayar: String,
    var catatan: String,
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
        parcel.writeString(password)
        parcel.writeString(tanggallahir)
        parcel.writeString(email)
        parcel.writeString(alamat)
        parcel.writeString(nokamar)
        parcel.writeString(sewa)
        parcel.writeString(telepon)
        parcel.writeString(kontakdarurat)
        parcel.writeString(tanggalmasuk)
        parcel.writeString(tanggalkeluar)
        parcel.writeString(statusbayar)
        parcel.writeString(catatan)
        parcel.writeString(gambar)
    }

    // Describe contents (always return 0)
    override fun describeContents(): Int {
        return 0
    }

    // Companion object to create instances of Parcelable class from a Parcel
    companion object CREATOR : Parcelable.Creator<PengelolaModel> {
        override fun createFromParcel(parcel: Parcel): PengelolaModel {
            return PengelolaModel(parcel)
        }

        override fun newArray(size: Int): Array<PengelolaModel?> {
            return arrayOfNulls(size)
        }
    }
}





