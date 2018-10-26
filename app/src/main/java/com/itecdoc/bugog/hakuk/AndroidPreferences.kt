package com.itecdoc.bugog.hakuk

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import kotlin.reflect.KProperty

class AndroidPreferences(context: Context) : Preferences {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)

    override var login by PrefsDelegate(preferences, KEY_LOGIN, "")
    override var password by PrefsDelegate(preferences, KEY_PASSWORD, "")

    private class PrefsDelegate<T>(
        private val preferences: SharedPreferences,
        private val key: String,
        private val defaultValue: T,
        private val saveImmediately: Boolean = false,
        private val setTransform: ((T) -> T)? = null
    ) {
        @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return when (defaultValue) {
                is Boolean -> preferences.getBoolean(key, defaultValue)
                is String -> preferences.getString(key, defaultValue)
                is Int -> preferences.getInt(key, defaultValue)
                is Long -> preferences.getLong(key, defaultValue)
                is Float -> preferences.getFloat(key, defaultValue)
                is Set<*> -> preferences.getStringSet(key, defaultValue as Set<String>)
                else -> throw IllegalArgumentException("Invalid type for shared preferences!")
            } as T
        }

        @Suppress("UNCHECKED_CAST")
        @SuppressLint("ApplySharedPref")
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            val editor: SharedPreferences.Editor = preferences.edit()
            val result = setTransform?.let { it(value) } ?: value
            when (result) {
                is Boolean -> editor.putBoolean(key, result)
                is String -> editor.putString(key, result)
                is Int -> editor.putInt(key, result)
                is Long -> editor.putLong(key, result)
                is Float -> editor.putFloat(key, result)
                is Set<*> -> editor.putStringSet(key, result as Set<String>)
                else -> throw IllegalArgumentException("Invalid type for shared preferences!")
            }
            if (saveImmediately) editor.commit() else editor.apply()
        }
    }


    companion object {
        private const val KEY_LOGIN = "KEY_LOGIN"
        private const val KEY_PASSWORD = "KEY_PASSWORD"
    }
}