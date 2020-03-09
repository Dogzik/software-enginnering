package statistics.query

import statistics.model.UserStatistics
import statistics.state.StatisticsState

class StatisticsStateQueryDao(private val state: StatisticsState) : StatisticsQueryDao {
    override fun getUserStatistics(userId: Int): UserStatistics? = state.getUserStatistics(userId)
}
