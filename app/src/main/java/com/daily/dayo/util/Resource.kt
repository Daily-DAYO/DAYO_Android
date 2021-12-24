package com.daily.dayo.util

data class Resource<out T>(
    val status: Status, val data: T?, val message:String?
){
    // create some utility files to retrieve data inside other package.
    // These classes will be used to wrap our data to be used in a generic way into our UI.
    // 사용할 데이터를 Wrapping하여 Success, error, loading 판단
    companion object{
        fun <T> success(data:T?): Resource<T>{
            return Resource(Status.SUCCESS, data, null)
        }
        fun <T> error(msg:String, data:T?): Resource<T>{
            return Resource(Status.ERROR, data, msg)
        }
        fun <T> loading(data:T?): Resource<T>{
            return Resource(Status.LOADING, data, null)
        }
    }
}
