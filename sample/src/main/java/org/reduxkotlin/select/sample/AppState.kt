package org.reduxkotlin.select.sample

data class AppState(
    val counter: Int = 0,
    val isLoading: Boolean = false
)
