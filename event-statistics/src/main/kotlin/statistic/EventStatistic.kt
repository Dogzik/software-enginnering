package statistic

interface EventStatistic {
    fun incEvent(name: String)
    fun getEventStatisticByName(name: String): Double
    fun getAllEventsStatistic(): Map<String, Double>
    fun printStatistic()
}