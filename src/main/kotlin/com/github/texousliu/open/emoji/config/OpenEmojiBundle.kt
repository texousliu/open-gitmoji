package com.github.texousliu.open.emoji.config

import com.intellij.DynamicBundle
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.PropertyKey

class OpenEmojiBundle private constructor() : DynamicBundle(BUNDLE) {

    companion object {
        private const val BUNDLE = "messages.OpenEmojiBundle"

        @JvmStatic
        private val INSTANCE: OpenEmojiBundle = OpenEmojiBundle()

        @NotNull
        @Nls
        @JvmStatic
        fun message(@NotNull @PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: @NotNull Any?): String {
            return if (INSTANCE.containsKey(key)) INSTANCE.getMessage(key, params) else key
        }

//        @NotNull
//        @JvmStatic
//        fun messagePointer(@NotNull @PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: @NotNull Any?): Supplier<String> {
//            return if (INSTANCE.containsKey(key)) INSTANCE.getLazyMessage(key, params) else Supplier { key }
//        }

    }

}