/*
 * The MIT License (MIT)
 *
 *     Copyright (c) 2017-2020 StarryBlueSky
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

@file:Suppress("UNUSED")

package blue.starry.penicillin.extensions.cursor

import blue.starry.penicillin.core.exceptions.PenicillinException
import blue.starry.penicillin.core.i18n.LocalizedString
import blue.starry.penicillin.core.request.action.ApiAction
import blue.starry.penicillin.core.request.action.CursorJsonObjectApiAction
import blue.starry.penicillin.core.request.parameters
import blue.starry.penicillin.core.response.CursorJsonObjectResponse
import blue.starry.penicillin.endpoints.Option
import blue.starry.penicillin.extensions.*
import blue.starry.penicillin.models.cursor.PenicillinCursorModel

/*
    Next operations
 */

/**
 * Next cursor value.
 */
public val CursorJsonObjectResponse<*>.nextCursor: Long
    get() = result.nextCursor
/**
 * If true, next cursor exists.
 */
public val CursorJsonObjectResponse<*>.hasNext: Boolean
    get() = nextCursor > 0
/**
 * New [ApiAction] with next cursor.
 */
public val <M: PenicillinCursorModel> CursorJsonObjectResponse<M>.next: CursorJsonObjectApiAction<M>
    get() = byCursor(nextCursor)

/*
    Previous operations
 */

/**
 * Previous cursor value.
 */
public val CursorJsonObjectResponse<*>.previousCursor: Long
    get() = result.previousCursor
/**
 * If true, previous cursor exists.
 */
public val CursorJsonObjectResponse<*>.hasPrevious: Boolean
    get() = previousCursor > 0
/**
 * New [ApiAction] with previous cursor.
 */
public val <M: PenicillinCursorModel> CursorJsonObjectResponse<M>.previous: CursorJsonObjectApiAction<M>
    get() = byCursor(previousCursor)

/*
    Paging
 */

/**
 * New [ApiAction] with specified cursor.
 *
 * @param cursor Cursor value.
 * @param options options Optional. Custom parameters of this request.
 */
public fun <M: PenicillinCursorModel> CursorJsonObjectResponse<M>.byCursor(cursor: Long, vararg options: Option): CursorJsonObjectApiAction<M> {
    if (cursor == 0L) {
        throw PenicillinException(LocalizedString.CursorIsZero, null, request, response)
    }

    action as CursorJsonObjectApiAction<M>

    action.edit {
        parameters("cursor" to cursor, *options)
    }

    return CursorJsonObjectApiAction(client, action.request, action.converter)
}

/**
 * Retrieves all the responses with current [ApiAction].
 *
 * @param options options Optional. Custom parameters of this request.
 */
public fun <M: PenicillinCursorModel> CursorJsonObjectApiAction<M>.untilLast(vararg options: Option): Sequence<CursorJsonObjectResponse<M>> {
    return sequence {
        val first = complete()
        yield(first)

        var cursor = first.nextCursor
        while (cursor != 0L) {
            val result = first.byCursor(cursor, *options).complete()

            yield(result)
            cursor = result.nextCursor

            val rateLimit = result.rateLimit ?: continue
            if (rateLimit.isExceeded) {
                rateLimit.blockUntilRefresh()
            }
        }
    }
}

/**
 * Retrieves all the responses with current [ApiAction].
 *
 * @param options options Optional. Custom parameters of this request.
 */
public fun <M: PenicillinCursorModel> CursorJsonObjectResponse<M>.untilLast(vararg options: Option): Sequence<CursorJsonObjectResponse<M>> {
    return sequence {
        yield(this@untilLast)

        if (hasNext) {
            yieldAll(next.untilLast(*options))
        }
    }
}