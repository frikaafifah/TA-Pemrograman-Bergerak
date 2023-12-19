import android.os.Parcel
import android.os.Parcelable

class LaporanPengaduanModel(
    var no: String,
    var id: String,
    var userid: String,
    var judul: String,
    var deskripsi: String,
    var tanggal: String,
    var nama: String,
    var nokamar: String,
    var bukti:String
):Parcelable{


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
        parcel.readString() ?: ""
    )

    // Write to parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(no)
        parcel.writeString(id)
        parcel.writeString(userid)
        parcel.writeString(judul)
        parcel.writeString(deskripsi)
        parcel.writeString(tanggal)
        parcel.writeString(nama)
        parcel.writeString(nokamar)
        parcel.writeString(bukti)
    }

    // Describe contents (always return 0)
    override fun describeContents(): Int {
        return 0
    }

    // Companion object to create instances of Parcelable class from a Parcel
    companion object CREATOR : Parcelable.Creator<LaporanPengaduanModel> {
        override fun createFromParcel(parcel: Parcel): LaporanPengaduanModel {
            return LaporanPengaduanModel(parcel)
        }

        override fun newArray(size: Int): Array<LaporanPengaduanModel?> {
            return arrayOfNulls(size)
        }
    }
}


