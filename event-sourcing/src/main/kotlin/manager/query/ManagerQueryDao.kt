package manager.query

import manager.model.User

interface ManagerQueryDao {
    suspend fun getUser(userId: Int): User?
}
