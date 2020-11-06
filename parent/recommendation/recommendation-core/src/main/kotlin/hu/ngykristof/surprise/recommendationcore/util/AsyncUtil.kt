package hu.ngykristof.surprise.recommendationcore.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun asyncTask(block: suspend () -> Unit) = CoroutineScope(Dispatchers.Default).launch {
    block()
}