package com.smarinello.themoviedb.repository.remote

/**
 * Class to represent a [Success] or [Error] when calling the API.
 */
sealed class ResponseResult<out T : Any> {
    /**
     * Represents a successful API call.
     */
    class Success<out T : Any>(val data: T) : ResponseResult<T>()

    /**
     * Represents an unsuccessful API call.
     */
    class Error(val exception: Throwable) : ResponseResult<Nothing>()
}
