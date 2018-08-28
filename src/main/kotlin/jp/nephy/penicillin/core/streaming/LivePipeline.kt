package jp.nephy.penicillin.core.streaming

import com.google.gson.JsonObject
import jp.nephy.jsonkt.*
import jp.nephy.penicillin.models.special.StatusID
import java.util.concurrent.ExecutorService

class LivePipelineHandler(override val listener: LivePipelineListener, override val executor: ExecutorService): StreamHandler<LivePipelineListener> {
    override fun handle(json: JsonObject) {
        executor.execute { listener.onRawJson(json) }

        val topic = json["topic"].string
        if (topic.startsWith("/tweet_engagement/")) {
            val id = StatusID(topic.split("/").last().toLong())
            val engagement = json["payload"]["tweet_engagement"].jsonObject

            when {
                engagement.contains("like_count") -> listener.onUpdateLikeCount(id, engagement["like_count"].int)
                engagement.contains("retweet_count") -> listener.onUpdateRetweetCount(id, engagement["retweet_count"].int)
                engagement.contains("reply_count") -> listener.onUpdateReplyCount(id, engagement["reply_count"].int)
                else -> listener.onUnhandledData(json)
            }
        } else {
            listener.onUnhandledData(json)
        }
    }
}

interface LivePipelineListener: StreamListener {
    fun onUpdateLikeCount(id: StatusID, count: Int) {}
    fun onUpdateRetweetCount(id: StatusID, count: Int) {}
    fun onUpdateReplyCount(id: StatusID, count: Int) {}
}