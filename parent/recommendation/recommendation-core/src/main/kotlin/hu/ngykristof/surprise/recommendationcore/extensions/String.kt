package hu.ngykristof.surprise.recommendationcore.extensions

fun String.executeCommand() {
    Runtime.getRuntime().exec(this)
}