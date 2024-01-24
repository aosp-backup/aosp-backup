package com.stevesoltys.aosp_backup.util

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success

fun <T> T.toSuccess(): Success<T> {
  return Success(this)
}

fun <T> T.toFailure(): Failure<T> {
  return Failure(this)
}
