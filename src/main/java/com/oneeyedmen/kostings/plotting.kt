package com.oneeyedmen.kostings

import java.io.File
import java.util.concurrent.TimeUnit


fun Results.plotHistograms(outputDir: File) {
    batches.forEach {
        it.plotHistogram(outputDir.resolve(it.batchOptions.outputFilename + ".png"))
    }
}

fun Results.plotSamples(outputDir: File) {
    batches.forEach {
        if (it is JsonBatch)
            it.plotSamples(outputDir.resolve(it.batchOptions.outputFilename + ".samples.png"))
    }
}

fun Batch.plotHistogram(outputFile: File) {
    runPython("plot-histogram.py", summaryCsvFile, outputFile)
}

fun JsonBatch.plotSamples(outputFile: File) {
    runPython("plot-samples.py", samplesCsvFile, outputFile)
}

private fun runPython(script: String, input: File, outputFile: File) {
    ProcessBuilder("python", script, input.absolutePath, outputFile.absolutePath)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor(5, TimeUnit.SECONDS)
}

