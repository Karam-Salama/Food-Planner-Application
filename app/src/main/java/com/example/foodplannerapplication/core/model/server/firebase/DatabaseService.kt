package com.example.foodplannerapplication.core.model.server.firebase

abstract class DatabaseService {
    abstract suspend fun addData(
        path: String,
        data: Map<String, Any>,
        documentId: String? = null
    )

    abstract suspend fun getData(
        path: String,
        documentId: String? = null,
        query: Map<String, Any>? = null
    ): Any?

    abstract suspend fun checkIfDataExists(
        path: String,
        documentId: String
    ): Boolean
}

