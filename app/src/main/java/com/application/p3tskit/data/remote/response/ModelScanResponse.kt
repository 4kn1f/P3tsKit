import android.os.Parcel
import android.os.Parcelable

data class ModelScanResponse(
	val diseaseInfo: DiseaseInfo?,
	val predictedClass: String?,
	val userId: String?,
	val createdAt: Long
) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readParcelable(DiseaseInfo::class.java.classLoader),
		parcel.readString(),
		parcel.readString(),
		parcel.readLong()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeParcelable(diseaseInfo, flags)
		parcel.writeString(predictedClass)
		parcel.writeString(userId)
		parcel.writeLong(createdAt)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ModelScanResponse> {
		override fun createFromParcel(parcel: Parcel): ModelScanResponse {
			return ModelScanResponse(parcel)
		}

		override fun newArray(size: Int): Array<ModelScanResponse?> {
			return arrayOfNulls(size)
		}
	}
}

data class DiseaseInfo(
	val causes: String?,
	val description: String?,
	val note: String?,
	val symptoms: List<String> = emptyList(),
	val treatment: List<String> = emptyList(),
	val source: List<String> = emptyList()
) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.createStringArrayList() ?: emptyList(),
		parcel.createStringArrayList() ?: emptyList(),
		parcel.createStringArrayList() ?: emptyList()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(causes)
		parcel.writeString(description)
		parcel.writeString(note)
		parcel.writeStringList(symptoms)
		parcel.writeStringList(treatment)
		parcel.writeStringList(source)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<DiseaseInfo> {
		override fun createFromParcel(parcel: Parcel): DiseaseInfo {
			return DiseaseInfo(parcel)
		}

		override fun newArray(size: Int): Array<DiseaseInfo?> {
			return arrayOfNulls(size)
		}
	}
}
