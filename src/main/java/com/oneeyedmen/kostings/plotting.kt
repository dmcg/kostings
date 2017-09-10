package com.oneeyedmen.kostings

import java.io.File


fun Results.plot(outputDir: File) {
    batches.forEach {
        it.plot(outputDir.resolve(it.batchOptions.outputFilename + ".png"))
    }
}

fun Batch.plot(outputFile: File) {
    Runtime.getRuntime().exec(arrayOf("python", "plot.py", csvFile.absolutePath, outputFile.absolutePath))
}