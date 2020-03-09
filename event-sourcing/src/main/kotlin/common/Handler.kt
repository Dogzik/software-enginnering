package common

interface Handler<T> {
    suspend fun handle(task: T): String = try {
        doHandle(task)
    } catch (e: Exception) {
        "Got error during executing: ${e.message}"
    }

    suspend fun doHandle(task: T): String
}
