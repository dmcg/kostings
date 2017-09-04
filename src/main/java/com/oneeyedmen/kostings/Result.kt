package com.oneeyedmen.kostings

import java.math.BigDecimal

data class Result(
    val benchmarkName: String,
    val mode: String,
    val samples: Int,
    val score: BigDecimal,
    val error: BigDecimal?,
    val units: String
)