package com.application.p3tskit.data.remote.retrofit

import com.google.gson.JsonElement
import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import java.lang.reflect.Type

class CausesDeserializer : JsonDeserializer<List<String>> {
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: com.google.gson.JsonDeserializationContext?): List<String> {
        return if (json.isJsonArray) {
            json.asJsonArray.map { it.asString }
        } else {
            listOf(json.asString)
        }
    }
}

