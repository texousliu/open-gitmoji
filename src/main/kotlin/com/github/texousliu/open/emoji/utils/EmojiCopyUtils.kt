package com.github.texousliu.open.emoji.utils

import com.github.texousliu.open.emoji.model.OpenEmoji
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import okhttp3.*
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.math.floor

object EmojiCopyUtils {

    private const val URL = "https://emojicopy.com/assets/js/vendor/emoji.json"

    private fun loadFromEmojiCopy() {
        val client = OkHttpClient().newBuilder().addInterceptor(SafeGuardInterceptor()).build()
        val request: Request = Request.Builder()
            .url(URL)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("load emoji from https://emojicopy.com/ error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) println("load emoji from https://emojicopy.com/ error")
                    else {
                        val resp = InputStreamReader(response.body!!.byteStream())
                        val emojiSourceMap = Gson().fromJson(resp, JsonElement::class.java).asJsonObject
                        val map = emojiSourceMap.asMap().map {
                            handlerEmojiPoint(it.value)
                        }
                        val json = mapOf("emojis" to map)
                        val toJson = GsonBuilder().setPrettyPrinting().create().toJson(json)
                        val file = File("/Workspace/Java/open-gitmoji/src/main/resources/emojicopy.json")
                        if (!file.exists()) file.createNewFile()
                        file.bufferedWriter().use { out ->
                            out.write(toJson)
                        }
                    }
                }
            }
        })
    }

    fun handlerEmojiPoint(value: JsonElement) :OpenEmoji {
        val shortName = value.asJsonObject.get("shortname").asString
        val codePoints = value.asJsonObject.get("code_points").asJsonObject
        val point = (codePoints.get("fully_qualified") ?: codePoints.get("base")).asString
        val emoji = renderNativeEmoji(point)
        return OpenEmoji(emoji, "", shortName,
            shortName.replace(":", ""), "", "")
    }

    private fun renderNativeEmoji(point: String?): String {
        if (point == null) return ""
        if (point.indexOf("-") > -1) {
            val parts = mutableListOf<String>()
            val s = point.split("-")
            s.stream().map(EmojiCopyUtils::renderNativeEmojiItem).forEach(parts::add)
            return parts.joinToString("")
        } else {
            return renderNativeEmojiItem(point)
        }

    }

    private fun renderNativeEmojiItem(source: String): String {
        val part = Integer.parseInt(source, 16)
        if (part in 0x10000..0x10FFFF) {
            val hi: Int = floor(((part - 0x10000) / 0x400).toDouble()).toInt() + 0xD800;
            val lo: Int = ((part - 0x10000) % 0x400) + 0xDC00;
            return (hi.toChar().toString() + lo.toChar().toString())
        } else {
            return (part.toChar().toString())
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        loadFromEmojiCopy()
    }

}