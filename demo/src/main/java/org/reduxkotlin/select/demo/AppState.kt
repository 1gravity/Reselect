package org.reduxkotlin.select.demo

data class AppState(
    val counter: Int = 0,
    val isLoading: Boolean = false
)
