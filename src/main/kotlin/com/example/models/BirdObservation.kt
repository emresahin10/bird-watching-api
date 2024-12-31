package com.example.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Location(
    val latitude: Double,
    val longitude: Double
)

data class BirdObservation(
    @BsonId
    val id: ObjectId = ObjectId(),
    val birdId: ObjectId,
    val userId: ObjectId,
    val location: Location,
    val observationDate: Long,
    val notes: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 