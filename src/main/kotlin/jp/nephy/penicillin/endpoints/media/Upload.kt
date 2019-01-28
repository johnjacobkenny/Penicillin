/*
 * The MIT License (MIT)
 *
 *     Copyright (c) 2017-2019 Nephy Project Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

@file:Suppress("UNUSED", "PublicApiImplicitType")

package jp.nephy.penicillin.endpoints.media

import jp.nephy.penicillin.core.request.action.DelegatedAction
import jp.nephy.penicillin.endpoints.Media
import jp.nephy.penicillin.extensions.await
import jp.nephy.penicillin.extensions.endpoints.uploadAppend
import kotlinx.io.InputStream
import kotlin.math.ceil

private const val segmentMaxSize = 5 * 1024 * 1024

/**
 * Use this endpoint to upload images to Twitter. It returns a media_id which can be used in most Twitter endpoints that accept images. For example, a media_id value can be used to create a Tweet with an attached photo using the [POST statuses/update](https://developer.twitter.com/en/docs/tweets/post-and-engage/api-reference/post-statuses-update#post-statuses-update) endpoint.
 * This is a simple image upload endpoint, with a limited set of features. The preferred alternative is the chunked upload endpoint which supports both images and videos, provides better reliability, allows resumption of file uploads, and other important features. In the future, new features will only be supported for the chunked upload endpoint.
 * 
 * See the [Uploading media guide](https://developer.twitter.com/en/docs/media/upload-media/uploading-media/media-best-practices) for constraints and requirements on media files.
 * Use the [media metadata endpoint](https://developer.twitter.com/en/docs/media/upload-media/api-reference/post-media-metadata-create) to provide image alt text information.
 * Ensure the POST is a multipart/form-data request. Either upload the raw binary (media parameter) of the file, or its base64-encoded contents (media_data parameter). Use raw binary when possible, because base64 encoding results in larger file sizes
 * The response provides a media identifier in the media_id (64-bit integer) and media_id_string (string) fields. Use the media_id_string provided in the API response from JavaScript and other languages that cannot accurately represent a long integer.
 * The returned media_id is only valid for expires_after_secs seconds. Any attempt to use media_id after this time period in other endpoints will result in an HTTP 4xx Bad Request.
 * The additional_owners field enables media to be uploaded media as user A and then used to create Tweets as user B.
 * Please note that for certain types of data (tweet_gif, tweet_video and amplify_video), you need to use the [chunked upload end-point](https://developer.twitter.com/en/docs/media/upload-media/api-reference/post-media-upload-init).
 * 
 * [Twitter API reference](https://developer.twitter.com/en/docs/media/upload-media/api-reference/post-media-upload)
 *
 * @receiver [Media] endpoint instance.
 * @return [DelegatedAction] for [jp.nephy.penicillin.models.Media] model.
 */
fun Media.uploadMedia(
    input: InputStream,
    mediaType: MediaType,
    mediaCategory: MediaCategory? = null
) = DelegatedAction(client) {
    @Suppress("BlockingMethodInNonBlockingContext")
    val init = uploadInit(input.available(), mediaType, mediaCategory).await()

    input.use {
        val segmentCount = ceil(it.available().toDouble() / segmentMaxSize).toInt()
        
        repeat(segmentCount) { i ->
            val part = ByteArray(minOf(segmentMaxSize, it.available()))
            it.read(part)
            
            uploadAppend(part, mediaType, init.result.mediaId, i).await()
        }
    }

    uploadFinalize(init.result.mediaId).await().result
}
