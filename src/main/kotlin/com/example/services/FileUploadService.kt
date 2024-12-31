package com.example.services

import io.ktor.http.content.*
import java.io.File
import java.util.*

object FileUploadService {
    private const val UPLOAD_DIR = "uploads"
    
    init {
        File(UPLOAD_DIR).mkdirs()
    }
    
    suspend fun saveFile(part: PartData.FileItem): String {
        val originalFileName = part.originalFileName ?: return ""
        val fileExtension = originalFileName.substringAfterLast(".", "")
        val fileName = "${UUID.randomUUID()}.$fileExtension"
        val filePath = "$UPLOAD_DIR/$fileName"
        
        val file = File(filePath)
        part.streamProvider().use { input ->
            file.outputStream().buffered().use { output ->
                input.copyTo(output)
            }
        }
        
        return fileName
    }
    
    fun getFile(fileName: String): File {
        return File("$UPLOAD_DIR/$fileName")
    }
    
    fun deleteFile(fileName: String): Boolean {
        return try {
            File("$UPLOAD_DIR/$fileName").delete()
        } catch (e: Exception) {
            false
        }
    }
} 