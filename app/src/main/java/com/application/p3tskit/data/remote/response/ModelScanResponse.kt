package com.application.p3tskit.data.remote.response

import com.google.gson.annotations.SerializedName

data class ModelScanResponse(

	@field:SerializedName("createdAt")
	val createdAt: Long? = null,

	@field:SerializedName("disease_info")
	val diseaseInfo: DiseaseInfo? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("predicted_class")
	val predictedClass: String? = null
)

data class DiseaseInfo(

	@field:SerializedName("symptoms")
	val symptoms: String? = null,

	@field:SerializedName("note")
	val note: String? = null,

	@field:SerializedName("treatment")
	val treatment: List<String?>? = null,

	@field:SerializedName("causes")
	val causes: List<String?>? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("source")
	val source: List<String?>? = null
)
