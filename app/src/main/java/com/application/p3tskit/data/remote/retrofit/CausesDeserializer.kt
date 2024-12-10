package com.application.p3tskit.data.remote.retrofit

import com.google.gson.JsonElement
import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import java.lang.reflect.Type

/**
 * Custom deserializer for handling various JSON structures and converting them into a list of strings.
 */
class CausesDeserializer : JsonDeserializer<List<String>> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: com.google.gson.JsonDeserializationContext?
    ): List<String> {
        return when {
            json.isJsonArray -> handleJsonArray(json)
            json.isJsonObject -> handleJsonObject(json)
            json.isJsonPrimitive -> listOf(json.asString)
            json.isJsonNull -> listOf("null")
            else -> throw JsonParseException("Unsupported JSON element type: ${json.javaClass.simpleName}")
        }
    }

    /**
     * Handles JSON arrays and converts them to a list of strings.
     */
    private fun handleJsonArray(json: JsonElement): List<String> {
        return json.asJsonArray.map { element ->
            when {
                element.isJsonPrimitive -> element.asString
                element.isJsonObject -> element.toString()
                element.isJsonArray -> element.asJsonArray.joinToString(", ") { it.asString }
                element.isJsonNull -> "null"
                else -> throw JsonParseException("Unsupported JSON element in array: ${element.javaClass.simpleName}")
            }
        }
    }

    /**
     * Handles JSON objects and converts their values into a list of strings.
     */
    private fun handleJsonObject(json: JsonElement): List<String> {
        val resultList = mutableListOf<String>()
        val jsonObject = json.asJsonObject

        for ((key, value) in jsonObject.entrySet()) {
            when {
                value.isJsonPrimitive -> resultList.add("$key: ${value.asString}")
                value.isJsonArray -> resultList.add("$key: ${value.asJsonArray.joinToString(", ") { it.asString }}")
                value.isJsonObject -> resultList.add("$key: ${value.toString()}")
                value.isJsonNull -> resultList.add("$key: null")
                else -> throw JsonParseException("Unsupported JSON element type: ${value.javaClass.simpleName}")
            }
        }
        return resultList
    }
}
