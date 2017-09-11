package com.oneeyedmen.kostings

import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.options.CommandLineOptions
import java.io.File


private val defaultPatterns = listOf("baselines", "strings", "primitives", "let")
private val baseOptions = BatchOptions("*", forks = 1, warmups = 10, measurements = 10)
private val resultsDir = File("results")
private val imagesDir = resultsDir.resolve("images").apply { mkdirs() }

fun main(args: Array<String>) {

    val commandLineOptions = CommandLineOptions(*args)
    runConfig.batchOptions = baseOptions.applyOptions(commandLineOptions)
    runConfig.patterns = if (commandLineOptions.includes.isNotEmpty()) commandLineOptions.includes else defaultPatterns

    runConfig.results.plot(imagesDir)

    val testClasses = runConfig.results.allResults.toBenchmarkClasses().toTypedArray()
    val testResult = runTests(*testClasses)
    System.exit(if (testResult.wasSuccessful()) 0 else 1)
}


object runConfig {
    // this nastiness allows main to override with command-line options but JUnit tests to have access to un-overridden
    // without main being run
    var patterns: List<String> = defaultPatterns
    var batchOptions: BatchOptions = baseOptions

    val results by lazy { Results(patterns, batchOptions).apply { readOrRun(resultsDir, ResultFormatType.JSON) } }
}


private fun Map<*, Result>.toBenchmarkClasses(): List<Class<*>> =
    values.map { it.benchmarkName.toClassName() }.toSet().map { Class.forName(it) }


private fun String.toClassName() = this.substringBeforeLast('.')

