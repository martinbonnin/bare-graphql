package net.mbonnin.bare.graphql

import kotlinx.serialization.json.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


fun String.escape() = this.replace("\"", "\\\"")

fun variable(name: String) = "${'$'}$name"

fun graphQL(operation: String, variables: Map<String, String> = emptyMap(), headers: Map<String, String> = emptyMap()): Map<String, Any?> {
    val response = mapOf(
        "query" to operation,
        "variables" to variables
    ).toJsonElement().toString()
        .let {
            Request.Builder()
                .post(it.toRequestBody("application/graphql+json".toMediaType()))
                .url("https://api.github.com/graphql")
                .build()
        }
        .let {
            OkHttpClient.Builder()
                .addInterceptor {
                    it.proceed(it.request().newBuilder()
                        .apply {
                            headers.forEach {
                                header(it.key, it.value)
                            }
                        }.build()
                    )
                }
                .build()
                .newCall(it).execute()
        }

    if (!response.isSuccessful) {
        error("Cannot execute GraphQL operation '$operation':\n${response.body?.source()?.readUtf8()}")
    }

    val responseText = response.body?.source()?.readUtf8() ?: error("Cannot read response body")
    return Json.parseToJsonElement(responseText).toAny().asMap
}

fun Any?.toJsonElement(): JsonElement = when (this) {
    is Map<*, *> -> JsonObject(this.asMap.mapValues { it.value.toJsonElement() })
    is List<*> -> JsonArray(map { it.toJsonElement() })
    is Boolean -> JsonPrimitive(this)
    is Number -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    null -> JsonNull
    else -> error("cannot convert $this to JsonElement")
}

fun JsonElement.toAny(): Any? = when (this) {
    is JsonObject -> this.mapValues { it.value.toAny() }
    is JsonArray -> this.map { it.toAny() }
    is JsonPrimitive -> {
        when {
            isString -> this.content
            this is JsonNull -> null
            else -> booleanOrNull ?: intOrNull ?: longOrNull ?: doubleOrNull ?: error("cannot decode $this")
        }
    }
    else -> error("cannot convert $this to Any")
}

inline fun <reified T> Any?.cast() = this as T

val Any?.asMap: Map<String, Any?>
    get() = this.cast()
val Any?.asList: List<Any?>
    get() = this.cast()
val Any?.asString: String
    get() = this.cast()
val Any?.asBoolean: String
    get() = this.cast()
val Any?.asNumber: Number
    get() = this.cast()
