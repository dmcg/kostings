package com.oneeyedmen.kostings

import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.CommandLineOptions
import java.io.File


private val defaultPatterns = listOf("baselines", "primitives", "invoking", "strings", "let", "mapping")
private val baseOptions = BatchOptions("*", forks = 10, warmups = 20, measurements = 20)

/**
 * Runs a set of JMH benchmarks, as specified by command line arguments or the defaults above, and produces plots from
 * the result files.
 */
object Benchmarking {

    @JvmStatic
    fun main(args: Array<String>) {
        val commandLineOptions = CommandLineOptions(*args)
        val batchOptions = baseOptions.applyOptions(commandLineOptions)
        val patterns = if (commandLineOptions.includes.isNotEmpty()) commandLineOptions.includes else defaultPatterns

        readOrRunBenchmarks(patterns, batchOptions, Directories.resultsDir)
    }

    private fun readOrRunBenchmarks(patterns: List<String>, batchOptions: BatchOptions, outputDir: File) =
        patterns.map { readOrRunBenchmark(batchOptions.copy(pattern = it), outputDir) }

    private fun readOrRunBenchmark(batchOptions: BatchOptions, outputDir: File) {
        val file = outputDir.resolve(batchOptions.outputFilename + ".json")
        if (!file.isAResultFile())
            runBenchmark(batchOptions, file)
    }

    private fun runBenchmark(batchOptions: BatchOptions, outputFile: File) {
        outputFile.parentFile.mkdirs()
        val optionsWithOutput = batchOptions.toOptions().result(outputFile.absolutePath).resultFormat(ResultFormatType.JSON).build()
        Runner(optionsWithOutput).run()
    }
}




