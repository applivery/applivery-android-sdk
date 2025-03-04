package com.applivery.android.sdk.domain.model

import java.util.Date

data class User(
    val id: String,
    val email: String?,
    val firstName: String?,
    val fullName: String?,
    val lastName: String?,
    val type: UserType?,
    val createdAt: Date?
)

enum class UserType {
    User,
    Employee
}
