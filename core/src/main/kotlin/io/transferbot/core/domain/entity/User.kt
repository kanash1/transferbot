package io.transferbot.core.domain.entity

class User(
    var platformId: String?,
    var transferName: String,
    val transferId: Long? = null,
)