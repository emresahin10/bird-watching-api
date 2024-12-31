package com.example.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Bird(
    @BsonId
    val id: ObjectId = ObjectId(),
    val name: String,
    val scientificName: String,
    val habitat: List<String>,
    val photoUrl: String,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) 