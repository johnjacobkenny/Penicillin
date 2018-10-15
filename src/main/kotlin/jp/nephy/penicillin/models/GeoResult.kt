package jp.nephy.penicillin.models

import jp.nephy.jsonkt.JsonObject
import jp.nephy.jsonkt.delegation.byModelList
import jp.nephy.jsonkt.delegation.immutableJsonObject
import jp.nephy.jsonkt.delegation.model

data class GeoResult(override val json: JsonObject): PenicillinModel {
    val query by model<GeoQuery>()

    private val jsonResult by immutableJsonObject("result")
    val result by jsonResult.byModelList<Place>(key = "places")
}
