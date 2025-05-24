package com.ruparts.app.core.utils

import kotlinx.coroutines.CancellationException

inline fun <T, R> T.runCoroutineCatching(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        Result.failure(e)
    }
}