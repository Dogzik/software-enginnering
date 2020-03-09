package manager.query

sealed class ManagerQuery

data class GetUserQuery(val userId: Int) : ManagerQuery()
