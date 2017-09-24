package com.oneeyedmen.kostings.matchers

import org.apache.commons.math3.stat.descriptive.StatisticalSummary

class OffsetStatistics(val innerStats: StatisticalSummary, val offset: Double) : StatisticalSummary {
    override fun getMax() = this.innerStats.max + offset

    override fun getVariance() = this.innerStats.variance

    override fun getN() = innerStats.n

    override fun getMin() = innerStats.min + offset

    override fun getMean() = innerStats.mean + offset

    override fun getStandardDeviation() = innerStats.standardDeviation

    override fun getSum() = innerStats.sum + offset*this.n
}