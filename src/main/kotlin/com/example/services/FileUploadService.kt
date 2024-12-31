package com.example.services

import io.ktor.http.content.*
import java.io.File
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.extension

object FileUploadService {
    private const val UPLOAD_DIR = "uploads"
    private const val MAX_FILE_SIZE = 5 * 1024 * 1024 // 5MB
    private val ALLOWED_EXTENSIONS = setOf("jpg", "jpeg", "png", "gif")
    
    init {
        File(UPLOAD_DIR).mkdirs()
    }

    data class FileValidationResult(
        val isValid: Boolean,
        val error: String? = null
    )
    
    suspend fun saveFile(part: PartData.FileItem): Result<String> {
        return try {
            // Validate file
            val validation = validateFile(part)
            if (!validation.isValid) {
                return Result.failure(Exception(validation.error))
            }

            val originalFileName = part.originalFileName ?: return Result.failure(Exception("No file name provided"))
            val fileExtension = Path(originalFileName).extension.lowercase()
            val fileName = "${UUID.randomUUID()}.$fileExtension"
            val filePath = "$UPLOAD_DIR/$fileName"
            
            val file = File(filePath)
            part.streamProvider().use { input ->
                file.outputStream().buffered().use { output ->
                    input.copyTo(output)
                }
            }
            
            Result.success(fileName)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to save file: ${e.message}"))
        } finally {
            part.dispose()
        }
    }
    
    private suspend fun validateFile(part: PartData.FileItem): FileValidationResult {
        // Check file name
        val fileName = part.originalFileName ?: return FileValidationResult(
            false,
            "No file name provided"
        )

        // Check file extension
        val extension = Path(fileName).extension.lowercase()
        if (extension !in ALLOWED_EXTENSIONS) {
            return FileValidationResult(
                false,
                "File type not allowed. Allowed types: ${ALLOWED_EXTENSIONS.joinToString(", ")}"
            )
        }

        // Check file size
        var size = 0L
        part.streamProvider().use { input ->
            size = input.available().toLong()
        }
        
        if (size > MAX_FILE_SIZE) {
            return FileValidationResult(
                false,
                "File size exceeds maximum limit of ${MAX_FILE_SIZE / 1024 / 1024}MB"
            )
        }

        return FileValidationResult(true)
    }
    
    fun getFile(fileName: String): Result<File> {
        return try {
            val file = File("$UPLOAD_DIR/$fileName")
            if (!file.exists()) {
                Result.failure(Exception("File not found"))
            } else if (!isFileNameValid(fileName)) {
                Result.failure(Exception("Invalid file name"))
            } else {
                Result.success(file)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun deleteFile(fileName: String): Result<Boolean> {
        return try {
            if (!isFileNameValid(fileName)) {
                return Result.failure(Exception("Invalid file name"))
            }

            val file = File("$UPLOAD_DIR/$fileName")
            if (!file.exists()) {
                Result.failure(Exception("File not found"))
            } else {
                Result.success(file.delete())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isFileNameValid(fileName: String): Boolean {
        // Check if file name matches UUID pattern with allowed extension
        val uuidPattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
        val extensionPattern = ALLOWED_EXTENSIONS.joinToString("|")
        val pattern = "$uuidPattern\\.($extensionPattern)$".toRegex(RegexOption.IGNORE_CASE)
        return pattern.matches(fileName)
    }
} 