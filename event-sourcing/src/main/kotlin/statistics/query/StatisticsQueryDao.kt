package statistics.query

import statistics.model.UserStatistics

interface StatisticsQueryDao {
    fun getUserStatistics(userId: Int): UserStatistics?
}
