package com.oneeyedmen.kostings

import java.io.File

interface Batch {
    val batchOptions: BatchOptions
    val dataFile: File
    val results: List<Result>
    val csvFile: File
}

