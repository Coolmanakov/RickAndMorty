package com.example.rickmorty.util

sealed class UserAction<Any>(){
    data class FilterAction<T>(val filter: T): UserAction<T>()
    class FindAllItemAction: UserAction<Any>()
    data class FindItemByIdsAction(val idList: List<Int>): UserAction<Any>()
}
