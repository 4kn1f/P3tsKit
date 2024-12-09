package com.application.p3tskit.data.remote.retrofit

import com.google.gson.JsonElement
import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class CausesDeserializer : JsonDeserializer<List<String>> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: com.google.gson.JsonDeserializationContext?): List<String> {
        return when {
            json.isJsonArray -> {
                json.asJsonArray.map { it.asString }
            }
            json.isJsonObject -> {
                val resultList = mutableListOf<String>()
                val jsonObject = json.asJsonObject

                for (entry in jsonObject.entrySet()) {
                    val value = entry.value
                    if (value.isJsonPrimitive) {
                        resultList.add(value.asString)
                    } else if (value.isJsonArray) {
                        resultList.add(value.asJsonArray.joinToString(", ") { it.asString })
                    } else if (value.isJsonObject) {
                        resultList.add(value.toString())
                    } else if (value.isJsonNull) {
                        resultList.add("null")
                    }
                }
                resultList
            }
            json.isJsonPrimitive -> {
                listOf(json.asString)
            }
            json.isJsonNull -> {

                listOf("null")
            }
            else -> {

                throw JsonParseException("Unsupported JSON element type: ${json.javaClass.simpleName}")
            }
        }
    }
}
