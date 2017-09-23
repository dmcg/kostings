package com.oneeyedmen.kostings

import java.io.File
import java.util.concurrent.TimeUnit

fun Batch.plotHistogramTo(outputFile: File) =
    runPython("plot-histogram.py", summaryCsvFile, outputFile)

fun JsonBatch.plotSamplesTo(outputFile: File) =
    runPython("plot-samples.py", samplesCsvFile, outputFile)

fun Batch.plotIn(outputDir: File) {
    plotHistogramTo(outputDir.resolve(batchOptions.outputFilename + ".png"))
    if (this is JsonBatch)
        plotSamplesTo(outputDir.resolve(batchOptions.outputFilename + ".samples.png"))
}

fun Iterable<Batch>.plotIn(outputDir: File) =
    forEach {
        it.plotIn(outputDir)
    }


private fun runPython(script: String, input: File, outputFile: File) {
    ProcessBuilder("python", script, input.absolutePath, outputFile.absolutePath)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor(5, TimeUnit.SECONDS)
}

