package com.oneeyedmen.kostings

import java.io.File
import java.util.concurrent.TimeUnit


object Plotting{

    @JvmStatic
    fun main(args: Array<String>) {
        val dir = File("raspi-results")
        readBatches(dir).asIterable().plotIn(dir, rebuild = false)
    }
}

fun Iterable<Batch>.plotIn(outputDir: File, rebuild: Boolean = false) =
    forEach {
        it.plotIn(outputDir, rebuild)
    }

fun ResultSet.plotHistogramTo(outputFile: File) =
    runPython("plot-histogram.py", tempFile(description + "-", ".csv", this::writeStatsCSV), outputFile)

fun Batch.plotSamplesTo(outputFile: File) =
    runPython("plot-samples.py", tempFile(description + "-", ".samples.csv", this::writeSamplesCSV), outputFile)

fun Batch.plotIn(outputDir: File, rebuild: Boolean = false) {
    onFile(outputDir.resolve(batchOptions.outputFilename + ".png"), rebuild, this::plotHistogramTo)
    onFile(outputDir.resolve(batchOptions.outputFilename + ".samples.png"), rebuild, this::plotSamplesTo)
}

private fun onFile(file: File, rebuild: Boolean = false, builder: (File) -> Unit) {
    if (!file.isFile || rebuild) builder(file)
}

private fun runPython(script: String, input: File, outputFile: File) {
    ProcessBuilder("python", script, "${input.absolutePath}", "${outputFile.absolutePath}")
        .inheritIO()
        .start()
        .waitFor(5, TimeUnit.SECONDS)
}

private fun tempFile(prefix: String, suffix: String, writer: (File) -> Unit): File =
    File.createTempFile(prefix, suffix).apply {
        writer(this)
    }
