package com.applivery.data.response

import com.applivery.base.domain.model.UserProfile
import com.applivery.base.domain.model.UserType
import java.text.SimpleDateFormat
import java.util.Locale

fun UserEntity.toDomain(): UserProfile = UserProfile(
    id = id.orEmpty(),
    email = email,
    firstName = firstName,
    fullName = fullName,
    lastName = lastName,
    type = type?.toDomain(),
    createdAt = createdAt?.let {
        runCatching {
            SimpleDateFormat(
                ServerResponse.ApiDateFormat,
                Locale.getDefault()
            ).parse(it)
        }.getOrNull()
    }
)

fun ApiUserType.toDomain(): UserType = when (this) {
    ApiUserType.User -> UserType.User
    ApiUserType.Employee -> UserType.Employee
}
