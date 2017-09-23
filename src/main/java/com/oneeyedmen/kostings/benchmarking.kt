package com.oneeyedmen.kostings

import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.CommandLineOptions
import java.io.File


private val defaultPatterns = listOf("baselines", "strings", "primitives", "let")
private val baseOptions = BatchOptions("*", forks = 10, warmups = 20, measurements = 20)

val resultsDir = File("results")
val canonicalResultsDir = File("canonical-results")
val imagesDir = resultsDir.resolve("images").apply { mkdirs() }

object benchmarking {
    @JvmStatic
    fun main(args: Array<String>) {

        val commandLineOptions = CommandLineOptions(*args)
        val batchOptions = baseOptions.applyOptions(commandLineOptions)
        val patterns = if (commandLineOptions.includes.isNotEmpty()) commandLineOptions.includes else defaultPatterns

        readOrRunBenchmarks(patterns, batchOptions, resultsDir, ResultFormatType.JSON)
            .plotIn(imagesDir)
    }
}

fun readOrRunBenchmarks(patterns: List<String>, batchOptions: BatchOptions, outputDir: File, resultFormatType: ResultFormatType) =
    patterns.map { readOrRunBenchmark(batchOptions.copy(pattern = it), outputDir, resultFormatType) }

fun readOrRunBenchmark(batchOptions: BatchOptions, outputDir: File, resultFormatType: ResultFormatType): Batch {
    val file = outputDir.resolve(batchOptions.outputFilename + resultFormatType.toExtension())
    if (!file.isFile)
        runBenchmark(batchOptions, file, resultFormatType)
    return resultFormatType.readBatchFromFile(batchOptions, file)
}

private fun ResultFormatType.readBatchFromFile(batchOptions: BatchOptions, file: File) = when (this) {
    ResultFormatType.CSV-> readBatchFromCsv(batchOptions, file)
    ResultFormatType.JSON -> readBatchFromJson(batchOptions, file)
    else -> throw IllegalArgumentException("Unsupported result format type $this")
}

private fun ResultFormatType.toExtension() = when (this) {
    ResultFormatType.CSV-> ".csv"
    ResultFormatType.JSON -> ".json"
    else -> IllegalArgumentException("Unsupported result format type $this")
}

private fun runBenchmark(batchOptions: BatchOptions, outputFile: File, resultFormatType: ResultFormatType) {
    outputFile.parentFile.mkdirs()
    val optionsWithOutput = batchOptions.toOptions().result(outputFile.absolutePath).resultFormat(resultFormatType).build()
    Runner(optionsWithOutput).run()
}



