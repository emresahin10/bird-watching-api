package com.example.repositories

import com.example.database.MongoDB
import com.example.models.User
import org.bson.types.ObjectId
import org.litote.kmongo.*

class UserRepository {
    private val collection = MongoDB.users

    suspend fun findAll(): List<User> = collection.find().toList()

    suspend fun findById(id: ObjectId): User? = collection.findOneById(id)

    suspend fun findByUserId(userId: String): User? =
        collection.findOne(User::userId eq userId)

    suspend fun findByEmail(email: String): User? =
        collection.findOne(User::email eq email)

    suspend fun findByRefreshToken(refreshToken: String): User? =
        collection.findOne(User::refreshToken eq refreshToken)

    suspend fun create(user: User): User {
        collection.insertOne(user)
        return user
    }

    suspend fun update(id: ObjectId, user: User): Boolean {
        val update = set(
            User::email setTo user.email,
            User::name setTo user.name,
            User::updatedAt setTo System.currentTimeMillis()
        )
        return collection.updateOneById(id, update).modifiedCount == 1L
    }

    suspend fun updateRefreshToken(id: ObjectId, refreshToken: String?, refreshTokenExpiresAt: Long?): Boolean {
        val update = set(
            User::refreshToken setTo refreshToken,
            User::refreshTokenExpiresAt setTo refreshTokenExpiresAt,
            User::updatedAt setTo System.currentTimeMillis()
        )
        return collection.updateOneById(id, update).modifiedCount == 1L
    }

    suspend fun delete(id: ObjectId): Boolean =
        collection.deleteOneById(id).deletedCount == 1L
} 