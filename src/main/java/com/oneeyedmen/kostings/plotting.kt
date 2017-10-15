package com.oneeyedmen.kostings

import java.io.File
import java.util.concurrent.TimeUnit

fun ResultSet.plotHistogramTo(outputFile: File) =
    runPython("plot-histogram.py", tempFile(description, ".csv", this::writeStatsCSV), outputFile)

fun Batch.plotSamplesTo(outputFile: File) =
    runPython("plot-samples.py", tempFile(description, ".samples.csv", this::writeSamplesCSV), outputFile)

fun Batch.plotIn(outputDir: File) {
    plotHistogramTo(outputDir.resolve(batchOptions.outputFilename + ".png"))
    plotSamplesTo(outputDir.resolve(batchOptions.outputFilename + ".samples.png"))
}

fun Iterable<Batch>.plotIn(outputDir: File) =
    forEach {
        it.plotIn(outputDir)
    }


private fun runPython(script: String, input: File, outputFile: File) {
    ProcessBuilder("python", script, "\"${input.absolutePath}\"", "\"${outputFile.absolutePath}\"")
        .inheritIO()
        .start()
        .waitFor(5, TimeUnit.SECONDS)
}

private fun tempFile(prefix: String, suffix: String, writer: (File) -> Unit): File =
    File.createTempFile(prefix, suffix).apply {
        writer(this)
    }
