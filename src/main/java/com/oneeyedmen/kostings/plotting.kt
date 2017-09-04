package com.oneeyedmen.kostings

import java.io.File


fun plot(batch: Batch, outputFile: File) {
    Runtime.getRuntime().exec(arrayOf("python", "plot.py", batch.csvFile.absolutePath, outputFile.absolutePath))
}