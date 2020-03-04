package hu.ngykristof.surprise.commonscore.extensions

import java.util.*

fun <T> Optional<T>.orNull(): T? = orElse(null)