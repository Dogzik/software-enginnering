package users.model

data class User(val name: String, val balance: Long, val shares: Map<String, Long>)
