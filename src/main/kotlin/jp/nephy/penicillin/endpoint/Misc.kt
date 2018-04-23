package jp.nephy.penicillin.endpoint

import jp.nephy.penicillin.PenicillinClient
import jp.nephy.penicillin.model.BadgeCount
import jp.nephy.penicillin.model.DMUserUpdate
import jp.nephy.penicillin.model.Empty


class Misc(override val client: PenicillinClient): Endpoint {
    @PrivateEndpoint
    fun badgeCount(vararg options: Pair<String, Any?>)= client.session.getObject<BadgeCount>("https://api.twitter.com/2/badge_count/badge_count.json") {
        query("cards_platform" to "iPhone-13", "contributor_details" to 1, "ext" to "altText,info360,mediaColor,mediaRestrictions,mediaStats,stickerInfo", "include_cards" to 1, "include_carousels" to 1, "include_entities" to 1, "include_ext_media_color" to true, "include_media_features" to true, "include_my_retweet" to 1, "include_profile_interstitial_type" to true, "include_profile_location" to true, "include_reply_count" to 1, "include_user_entities" to true, "include_user_hashtag_entities" to true, "include_user_mention_entities" to true, "include_user_symbol_entities" to true, "model_version" to 9, "tweet_mode" to "extended", "use_truncated_counts" to false, *options)
    }

    @PrivateEndpoint
    fun userUpdates(cursor: String, vararg options: Pair<String, Any?>)= client.session.getObject<DMUserUpdate>("/dm/user_updates.json") {
        query("cards_platform" to "iPhone-13", "contributor_details" to "1", "cursor" to cursor, "dm_users" to "true", "ext" to "altText,info360,mediaColor,mediaRestrictions,mediaStats,stickerInfo", "include_cards" to "1", "include_carousels" to "1", "include_entities" to "1", "include_ext_media_color" to "true", "include_groups" to "true", "include_inbox_timelines" to "true", "include_media_features" to "true", "include_my_retweet" to "1", "include_profile_interstitial_type" to "true", "include_profile_location" to "true", "include_reply_count" to "1", "include_user_entities" to "true", "include_user_hashtag_entities" to "true", "include_user_mention_entities" to "true", "include_user_symbol_entities" to "true", "tweet_mode" to "extended", *options)
    }

    @PrivateEndpoint
    fun promptSuggest(vararg options: Pair<String, Any?>)= client.session.getObject<Empty>("/prompts/suggest.json") {
        query("client_namespace" to "native", "consecutive_days" to "1", "force_fatigue_on_override" to "false", "format" to "home_timeline", "has_unknown_phone_number" to "false", "lang" to "ja", "notifications_device" to "true", *options)
    }
}
