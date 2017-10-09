package com.oneeyedmen.kostings

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics


/**
 * Represents the results of a single run of a benchmark (this may be many iterations, but only one JMH run).
 */
class IndividualBenchmarkResult(
    override val benchmarkName: String,
    override val mode: String,
    override val units: String,
    override val data: DescriptiveStatistics
) : Result {

    override fun toString() = _toString()
}


