package com.oneeyedmen.kostings

import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.options.CommandLineOptions
import java.io.File


private val defaultPatterns = listOf("baselines", "strings", "primitives", "let")
private val baseOptions = BatchOptions("*", forks = 1, warmups = 10, measurements = 10)

val resultsDir = File("results")
val canonicalResultsDir = File("canonical-results")
val imagesDir = resultsDir.resolve("images").apply { mkdirs() }

private fun main(args: Array<String>) {

    val commandLineOptions = CommandLineOptions(*args)
    runConfig.batchOptions = baseOptions.applyOptions(commandLineOptions)
    runConfig.patterns = if (commandLineOptions.includes.isNotEmpty()) commandLineOptions.includes else defaultPatterns

    runConfig.batches.plotHistograms(imagesDir)
    runConfig.batches.plotSamples(imagesDir)
}

object runConfig {
    // this nastiness allows main to override with command-line options but JUnit tests to have access to un-overridden
    // without main being run
    var patterns: List<String> = defaultPatterns
    var batchOptions: BatchOptions = baseOptions

    val batches by lazy { Batches(patterns, batchOptions).apply { readOrRun(resultsDir, ResultFormatType.JSON) } }
}




