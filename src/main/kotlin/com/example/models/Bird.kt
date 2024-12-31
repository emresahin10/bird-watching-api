package com.example.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import com.fasterxml.jackson.annotation.JsonProperty

data class Bird(
    @BsonId
    @JsonProperty("_id")
    val id: ObjectId = ObjectId(),
    val name: String,
    val scientificName: String,
    val habitat: List<String>,
    val photoUrl: String,
    val description: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // ID'yi string olarak döndüren yardımcı fonksiyon
    @JsonProperty("id")
    fun getStringId(): String = id.toHexString()
} 