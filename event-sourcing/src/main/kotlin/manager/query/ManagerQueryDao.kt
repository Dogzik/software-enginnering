package manager.query

import manager.model.User

interface ManagerQueryDao {
    suspend fun getUser(user_id: Int): User?
}
