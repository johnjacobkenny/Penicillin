package jp.nephy.penicillin.model

import com.github.salomonbrys.kotson.byString
import com.google.gson.JsonElement

@Suppress("UNUSED")
class Privacy(val json: JsonElement) {
    val privacy by json.byString
}
