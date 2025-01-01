package com.example.routes

import com.example.services.FileUploadService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.content.*

fun Route.fileRoutes() {
    route("/api/files") {
        // Public endpoint for viewing images
        get("/view/{fileName}") {
            val fileName = call.parameters["fileName"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "No fileName provided")
            )

            FileUploadService.getFile(fileName).fold(
                onSuccess = { file ->
                    // Set content type based on file extension
                    val contentType = when(fileName.substringAfterLast(".").lowercase()) {
                        "jpg", "jpeg" -> ContentType.Image.JPEG
                        "png" -> ContentType.Image.PNG
                        "gif" -> ContentType.Image.GIF
                        else -> ContentType.Image.Any
                    }
                    
                    call.response.header(
                        HttpHeaders.ContentType,
                        contentType.toString()
                    )
                    call.respondFile(file)
                },
                onFailure = { error ->
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to (error.message ?: "File not found"))
                    )
                }
            )
        }

        authenticate("auth-jwt") {
            post("/upload") {
                try {
                    val multipart = call.receiveMultipart()
                    val uploadResults = mutableListOf<Map<String, Any>>()
                    var filesProcessed = 0

                    multipart.forEachPart { part ->
                        if (part is PartData.FileItem) {
                            filesProcessed++
                            if (filesProcessed > 5) {
                                part.dispose()
                                return@forEachPart
                            }

                            val result = FileUploadService.saveFile(part)
                            result.fold(
                                onSuccess = { fileName ->
                                    // Add full URL to the response
                                    val fileUrl = "${call.request.local.scheme}://${call.request.local.host}:${call.request.local.port}/api/files/view/$fileName"
                                    uploadResults.add(mapOf(
                                        "fileName" to fileName,
                                        "fileUrl" to fileUrl,
                                        "status" to "success"
                                    ))
                                },
                                onFailure = { error ->
                                    uploadResults.add(mapOf(
                                        "error" to (error.message ?: "Unknown error"),
                                        "status" to "error"
                                    ))
                                }
                            )
                        }
                        part.dispose()
                    }

                    if (uploadResults.isEmpty()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "No files were uploaded")
                        )
                    } else {
                        call.respond(
                            if (uploadResults.any { it["status"] == "error" }) 
                                HttpStatusCode.PartialContent 
                            else 
                                HttpStatusCode.Created,
                            mapOf("files" to uploadResults)
                        )
                    }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Failed to process upload: ${e.message}")
                    )
                }
            }

            get("/download/{fileName}") {
                val fileName = call.parameters["fileName"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "No fileName provided")
                )

                FileUploadService.getFile(fileName).fold(
                    onSuccess = { file ->
                        call.response.header(
                            HttpHeaders.ContentDisposition,
                            ContentDisposition.Attachment.withParameter(
                                ContentDisposition.Parameters.FileName,
                                fileName
                            ).toString()
                        )
                        call.respondFile(file)
                    },
                    onFailure = { error ->
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to (error.message ?: "File not found"))
                        )
                    }
                )
            }

            delete("/{fileName}") {
                val fileName = call.parameters["fileName"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "No fileName provided")
                )

                FileUploadService.deleteFile(fileName).fold(
                    onSuccess = { deleted ->
                        if (deleted) {
                            call.respond(HttpStatusCode.NoContent)
                        } else {
                            call.respond(
                                HttpStatusCode.NotFound,
                                mapOf("error" to "File not found or could not be deleted")
                            )
                        }
                    },
                    onFailure = { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to (error.message ?: "Failed to delete file"))
                        )
                    }
                )
            }
        }
    }
} 