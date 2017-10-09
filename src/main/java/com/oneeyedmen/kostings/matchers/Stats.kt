package com.oneeyedmen.kostings.matchers

import org.apache.commons.math3.stat.descriptive.StatisticalSummary

interface Stats {
    val description : String
    val data: StatisticalSummary
}