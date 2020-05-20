package pl.edu.mimuw.students.wm382710.jnp.task03

import android.os.Handler
import android.os.Message
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt
import kotlin.random.Random

data class SimulationConstants(
    val initialAliveBeings: Int = 100,
    val initialSickBeings: Int = 4,
    val contactScaling: Double = 0.5,

    val infectionProbability: Double = 0.15,
    val deathProbability: Double = 0.06,
    val recoveryProbability: Double = 0.35,

    val durationSeconds: Long = 10,
    val healthySleepMs: Long = 1200,
    val sickSleepMs: Long = 1800
) {
    init {
        require(infectionProbability > 0) { "Infection probability is negative" }
        require(deathProbability > 0) { "Death probability is negative" }
        require(recoveryProbability > 0) { "Recovery probability is negative" }
        require(deathProbability + recoveryProbability <= 1) { "Death and recovery must be disjoint events in the probabilistic space" }
        require(durationSeconds > 0) { "Non positive duration" }
        require(initialAliveBeings > 0 && initialSickBeings > 0) { "Negative population parameter" }
    }

    val durationMs: Long = durationSeconds * 1000
}


class Simulation(val params : SimulationConstants) {
    companion object {
        const val HANDLE_TIMER_UPDATE = 1
        const val HANDLE_STATE_UPDATE = 2
    }

    val stateContext = newSingleThreadContext("SimulationContext")
    val simulationScope = CoroutineScope(Dispatchers.Default)

    var aliveThreads = AtomicInteger(params.initialAliveBeings)
    var sickThreads = params.initialSickBeings
    var deadThreads = 0

    var clockHandler: Handler? = null
    var stateUpdateHandler: Handler? = null

    fun start() {
        val simulationJob = simulationScope.launch {
            for (i in 1 .. params.initialAliveBeings) {
                launch { dailyCoRoutine(i) }
            }
        }
        println("Simulation started")
        GlobalScope.launch {
            for (i in 1 .. params.durationSeconds) {
                delay(1000)
                sendClockUpdate(params.durationSeconds - i)
            }
            simulationJob.cancelAndJoin()
            println("Simulation stopped")
        }
    }

    private suspend fun dailyCoRoutine(threadId: Int) {
        var isAlive = true
        var isSick = threadId <= params.initialSickBeings
        val rng = Random

        while (isAlive) {
            if (isSick) {
                delay(params.sickSleepMs)
                val roll = rng.nextDouble()
                if (roll < params.deathProbability) {
                    isAlive = false
                    withContext(stateContext) {
                        --sickThreads
                        aliveThreads.decrementAndGet()
                        ++deadThreads
                        sendStateUpdate()
                    }
                } else if (roll > 1 - params.recoveryProbability) {
                    isSick = false
                    withContext(stateContext) {
                        --sickThreads
                        sendStateUpdate()
                    }
                }
            } else {
                val contacted = (sickThreads * params.contactScaling).roundToInt()
                for (i in 1 .. contacted) {
                    val roll = rng.nextDouble()
                    if (roll < params.infectionProbability) {
                        isSick = true
                        withContext(stateContext) {
                            ++sickThreads
                            sendStateUpdate()
                        }
                        break;
                    }
                }
            }
        }
    }

    private fun sendStateUpdate() {
        val message = Message.obtain(stateUpdateHandler, HANDLE_STATE_UPDATE,
            Triple(aliveThreads, sickThreads, deadThreads))
        stateUpdateHandler?.sendMessage(message)
    }

    private fun sendClockUpdate(secRemaining: Long) {
        val message = Message.obtain(stateUpdateHandler, HANDLE_TIMER_UPDATE, secRemaining)
        clockHandler?.sendMessage(message)
    }
}

interface SimulationActivity {
    fun startSimulation(params: SimulationConstants) // like this?
}
