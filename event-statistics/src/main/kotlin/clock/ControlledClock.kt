package clock

import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount
import java.util.concurrent.atomic.AtomicReference

class ControlledClock(startTime: Instant) : Clock {
    private val curInstant = AtomicReference<Instant>(startTime)

    override fun now(): Instant = curInstant.get()

    fun setNow(newInstant: Instant) {
        curInstant.set(newInstant)
    }

    fun plus(duration: TemporalAmount) {
        curInstant.updateAndGet { it.plus(duration) }
    }

    fun minus(duration: TemporalAmount) {
        curInstant.updateAndGet { it.minus(duration) }
    }
}