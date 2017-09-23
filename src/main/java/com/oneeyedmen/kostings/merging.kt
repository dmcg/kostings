package com.oneeyedmen.kostings

fun Iterable<Result>.mergeResults(): Result = aggregateStats().let { stats ->
    Result(
        benchmarkName = this.allTheSame(Result::benchmarkName),
        mode = this.allTheSame(Result::mode),
        samplesCount = stats.samplesCount,
        score = stats.mean,
        error = stats.error,
        units = this.allTheSame(Result::units),
        samples = this.first().samples // as they won't be contemporaneous
    )
}

private fun <T> Iterable<Result>.allTheSame(property: (Result) -> T): T {
    // fold is nastier
    val firstValue = property(first())
    if (find { property(it) != firstValue } != null)
        throw IllegalArgumentException()
    else
        return firstValue
}

private val aStatsWeenie = true

private fun Iterable<Result>.aggregateStats(): Stats = when {
    aStatsWeenie -> first().stats
    else -> statsFor(this.mapNotNull(Result::samples))
}

private val Result.stats get() = Stats(samplesCount, score, error)

data class Stats(val samplesCount: Int, val mean: Double, val error: Double)

private fun statsFor(samples: Iterable<DoubleArray>): Stats = TODO("John")
