package com.application.p3tskit.data.remote.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ModelScanResponse(
	@SerializedName("disease_info") val diseaseInfo: DiseaseInfo?,
	@SerializedName("predicted_class") val predictedClass: String?,
	@SerializedName("user_id") val userId: String?,
	@SerializedName("createdAt") val createdAt: Long
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

	override fun describeContents(): Int = 0

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
	@SerializedName("causes") val causes: List<String> = emptyList(),
	@SerializedName("description") val description: String?,
	@SerializedName("note") val note: String?,
	@SerializedName("symptoms") val symptoms: List<String> = emptyList(),
	@SerializedName("treatment") val treatment: List<String> = emptyList(),
	@SerializedName("source") val source: List<String> = emptyList()
) : Parcelable {

	constructor(parcel: Parcel) : this(
		parcel.createStringArrayList() ?: emptyList(),
		parcel.readString(),
		parcel.readString(),
		parcel.createStringArrayList() ?: emptyList(),
		parcel.createStringArrayList() ?: emptyList(),
		parcel.createStringArrayList() ?: emptyList()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeStringList(causes)
		parcel.writeString(description)
		parcel.writeString(note)
		parcel.writeStringList(symptoms)
		parcel.writeStringList(treatment)
		parcel.writeStringList(source)
	}

	override fun describeContents(): Int = 0

	companion object CREATOR : Parcelable.Creator<DiseaseInfo> {
		override fun createFromParcel(parcel: Parcel): DiseaseInfo {
			return DiseaseInfo(parcel)
		}

		override fun newArray(size: Int): Array<DiseaseInfo?> {
			return arrayOfNulls(size)
		}
	}
}
