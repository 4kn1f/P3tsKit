package com.application.p3tskit.data.remote.response

data class HistoryResponses(
	val history: List<HistoryItem?>? = null
)

data class DiseasesInfo(
	val symptoms: List<String?>? = null,
	val note: String? = null,
	val treatment: List<String?>? = null,
	val causes: List<String?>? = null,
	val description: String? = null,
	val source: List<String?>? = null
)

data class HistoryItem(
	val createdAt: Long? = null,
	val diseaseInfo: DiseasesInfo? = null,
	val userId: String? = null,
	val predictedClass: String? = null
)

