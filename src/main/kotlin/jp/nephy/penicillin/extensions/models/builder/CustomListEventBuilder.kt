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

@file:Suppress("UNUSED")

package jp.nephy.penicillin.extensions.models.builder

import jp.nephy.jsonkt.edit
import jp.nephy.jsonkt.jsonObjectOf
import jp.nephy.penicillin.core.streaming.handler.UserStreamEvent
import jp.nephy.penicillin.models.UserStream
import java.util.*

class CustomListEventBuilder(type: UserStreamEvent): JsonBuilder<UserStream.ListEvent> {
    override var json = jsonObjectOf(
            "event" to type.key,
            "source" to null,
            "target" to null,
            "target_object" to null,
            "created_at" to null
    )

    private var source = CustomUserBuilder()
    fun source(builder: CustomUserBuilder.() -> Unit) {
        source.apply(builder)
    }

    private var target = CustomUserBuilder()
    fun target(builder: CustomUserBuilder.() -> Unit) {
        target.apply(builder)
    }

    private var targetObject = CustomListBuilder()
    fun targetObject(builder: CustomListBuilder.() -> Unit) {
        targetObject.apply(builder)
    }

    private var createdAt: Date? = null
    fun createdAt(date: Date? = null) {
        createdAt = date
    }

    override fun build(): UserStream.ListEvent {
        val source = source.build()
        val target = target.build()
        val targetObject = targetObject.build()

        return UserStream.ListEvent(json.edit {
            it["source"] = source
            it["target"] = target
            it["target_object"] = targetObject
            it["created_at"] = createdAt.toCreatedAt()
        })
    }
}