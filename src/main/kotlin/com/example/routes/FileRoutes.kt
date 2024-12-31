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
        authenticate("auth-jwt") {
            post("/upload") {
                val multipart = call.receiveMultipart()
                val fileNames = mutableListOf<String>()

                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) {
                        val fileName = FileUploadService.saveFile(part)
                        if (fileName.isNotEmpty()) {
                            fileNames.add(fileName)
                        }
                    }
                    part.dispose()
                }

                call.respond(hashMapOf("files" to fileNames))
            }

            get("/download/{fileName}") {
                val fileName = call.parameters["fileName"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "No fileName provided"
                )

                val file = FileUploadService.getFile(fileName)
                if (!file.exists()) {
                    call.respond(HttpStatusCode.NotFound, "File not found")
                } else {
                    call.respondFile(file)
                }
            }

            delete("/{fileName}") {
                val fileName = call.parameters["fileName"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    "No fileName provided"
                )

                if (FileUploadService.deleteFile(fileName)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound, "File not found or could not be deleted")
                }
            }
        }
    }
} 