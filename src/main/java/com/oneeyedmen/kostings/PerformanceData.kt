package com.oneeyedmen.kostings

import org.apache.commons.math3.stat.descriptive.StatisticalSummary

interface PerformanceData {
    val stats: StatisticalSummary
    val description : String
}