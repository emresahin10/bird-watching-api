package com.example.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User(
    @BsonId
    @Transient
    val id: ObjectId = ObjectId(),
    val userId: String = UUID.randomUUID().toString(),
    val email: String,
    val passwordHash: String,
    val name: String,
    val refreshToken: String? = null,
    val refreshTokenExpiresAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 