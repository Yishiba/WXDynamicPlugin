package com.wgllss.core.units

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive

/**
 * 执行[block]，[block]发生异常之后会被捕获并通知[onFailure] ([CancellationException]异常除外)，
 * 如果[onFailure]返回false则停止执行并返回失败结果；
 * 如果[onFailure]返回true则继续执行后面的逻辑，如果未达到最大执行次数[maxCount]，延迟[getDelay]之后继续执行[block]；
 * 如果达到最大执行次数[maxCount]，则返回失败结果，异常为[RetryMaxCountException]并携带最后一次的异常。
 */
suspend fun <T> fRetry(
    /** 最大执行次数 */
    maxCount: Int = 3,
    /** 获取延迟毫秒 */
    getDelay: RetryScope.() -> Long = { 5_000 },
    /** 失败回调，返回false停止执行 */
    onFailure: RetryScope.(Throwable) -> Boolean = { true },
    /** 执行回调 */
    block: suspend RetryScope.() -> T,
): Result<T> {
    require(maxCount > 0)
    with(RetryScopeImpl()) {
        while (true) {
            // 增加次数
            increaseCount()

            val result = runCatching {
                block()
            }.onFailure { e ->
                if (e is CancellationException) throw e
            }

            currentCoroutineContext().ensureActive()
            if (result.isSuccess) {
                return result
            }

            val exception = checkNotNull(result.exceptionOrNull())
            val shouldContinue = onFailure(exception).also { currentCoroutineContext().ensureActive() }
            if (!shouldContinue) {
                return result
            }

            if (currentCount >= maxCount) {
                // 达到最大执行次数
                return Result.failure(RetryMaxCountException(exception))
            } else {
                // 延迟后继续执行
                delay(getDelay())
                continue
            }
        }
    }
}

/** 达到最大执行次数异常 */
class RetryMaxCountException(cause: Throwable) : Exception(cause)

interface RetryScope {
    /** 当前执行次数 */
    val currentCount: Int
}

private class RetryScopeImpl : RetryScope {
    private var _count = 0

    override val currentCount: Int
        get() = _count

    fun increaseCount() {
        _count++
    }
}