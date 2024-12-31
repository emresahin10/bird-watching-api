package com.example.repositories

import com.example.database.MongoDB
import com.example.models.BirdObservation
import org.bson.types.ObjectId
import org.litote.kmongo.*

class BirdObservationRepository {
    private val collection = MongoDB.observations

    suspend fun findAll(): List<BirdObservation> = collection.find().toList()

    suspend fun findById(id: ObjectId): BirdObservation? = collection.findOneById(id)

    suspend fun findByUserId(userId: ObjectId): List<BirdObservation> =
        collection.find(BirdObservation::userId eq userId).toList()

    suspend fun findByBirdId(birdId: ObjectId): List<BirdObservation> =
        collection.find(BirdObservation::birdId eq birdId).toList()

    suspend fun create(observation: BirdObservation): BirdObservation {
        collection.insertOne(observation)
        return observation
    }

    suspend fun update(id: ObjectId, observation: BirdObservation): Boolean {
        val update = set(
            BirdObservation::location setTo observation.location,
            BirdObservation::observationDate setTo observation.observationDate,
            BirdObservation::notes setTo observation.notes,
            BirdObservation::photoUrl setTo observation.photoUrl,
            BirdObservation::updatedAt setTo System.currentTimeMillis()
        )
        return collection.updateOneById(id, update).modifiedCount == 1L
    }

    suspend fun delete(id: ObjectId): Boolean =
        collection.deleteOneById(id).deletedCount == 1L
} 