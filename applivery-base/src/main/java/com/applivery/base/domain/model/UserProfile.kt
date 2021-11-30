package com.applivery.base.domain.model

import java.util.Date

data class UserProfile(
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
