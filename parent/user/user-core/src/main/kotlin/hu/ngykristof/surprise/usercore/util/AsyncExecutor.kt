package hu.ngykristof.surprise.usercore.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun executeAsyncTask(block: suspend () -> Unit) = CoroutineScope(Dispatchers.Default).launch {
    block()
}
