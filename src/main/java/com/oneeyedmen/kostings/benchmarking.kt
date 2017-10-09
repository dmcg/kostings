package com.oneeyedmen.kostings

import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.CommandLineOptions
import java.io.File


private val defaultPatterns = listOf("baselines", "strings", "primitives", "let")
private val baseOptions = BatchOptions("*", forks = 10, warmups = 20, measurements = 20)

val resultsDir = File("results").apply { mkdirs() }
val canonicalResultsDir = File("canonical-results")
val imagesDir = resultsDir

object benchmarking {
    @JvmStatic
    fun main(args: Array<String>) {

        val commandLineOptions = CommandLineOptions(*args)
        val batchOptions = baseOptions.applyOptions(commandLineOptions)
        val patterns = if (commandLineOptions.includes.isNotEmpty()) commandLineOptions.includes else defaultPatterns

        readOrRunBenchmarks(patterns, batchOptions, resultsDir)
            .plotIn(imagesDir)
    }
}

fun readOrRunBenchmarks(patterns: List<String>, batchOptions: BatchOptions, outputDir: File) =
    patterns.map { readOrRunBenchmark(batchOptions.copy(pattern = it), outputDir) }

fun readOrRunBenchmark(batchOptions: BatchOptions, outputDir: File): Batch {
    val file = outputDir.resolve(batchOptions.outputFilename + ".json")
    if (!file.isFile)
        runBenchmark(batchOptions, file)
    return Batch.readFromJson(batchOptions, file)
}

private fun runBenchmark(batchOptions: BatchOptions, outputFile: File) {
    outputFile.parentFile.mkdirs()
    val optionsWithOutput = batchOptions.toOptions().result(outputFile.absolutePath).resultFormat(ResultFormatType.JSON).build()
    Runner(optionsWithOutput).run()
}



