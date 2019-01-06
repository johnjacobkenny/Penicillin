@file:Suppress("UNUSED")

package jp.nephy.penicillin.endpoints

import jp.nephy.penicillin.PenicillinClient
import jp.nephy.penicillin.models.Help

class Help(override val client: PenicillinClient): Endpoint {
    fun configuration(vararg options: Pair<String, Any?>) = client.session.get("/1.1/help/configuration.json") {
        parameter(*options)
    }.jsonObject<Help.Configuration>()

    fun languages(vararg options: Pair<String, Any?>) = client.session.get("/1.1/help/languages.json") {
        parameter(*options)
    }.jsonArray<Help.Language>()

    fun privacy(vararg options: Pair<String, Any?>) = client.session.get("/1.1/help/privacy.json") {
        parameter(*options)
    }.jsonObject<Help.Privacy>()

    fun tos(vararg options: Pair<String, Any?>) = client.session.get("/1.1/help/tos.json") {
        parameter(*options)
    }.jsonObject<Help.Tos>()

    @PrivateEndpoint
    fun setting(includeZeroRate: Boolean? = null, settingsVersion: String? = null, vararg options: Pair<String, Any?>) = client.session.get("/1.1/help/settings.json") {
        parameter("include_zero_rate" to includeZeroRate, "settings_version" to settingsVersion, *options)
    }.jsonObject<Help.Settings>()
}
