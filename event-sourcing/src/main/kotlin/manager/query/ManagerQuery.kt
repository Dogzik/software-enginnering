package manager.query

sealed class ManagerQuery

data class GetUserQuery(val user_id: Int) : ManagerQuery()
