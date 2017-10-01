package com.oneeyedmen.kostings

import org.apache.commons.math3.stat.inference.TestUtils.oneWayAnovaTest


fun hasAnomalousData(data: DoubleArray, alpha: Double = 0.01, chunkSize: Int = 50): Boolean {
    // can you reject the hypothesis that the chunks are the same w.r.t mean, and only have an alpha% chance of being wrong?
    return oneWayAnovaTest(data.inBatchesOf(chunkSize), alpha)
}


