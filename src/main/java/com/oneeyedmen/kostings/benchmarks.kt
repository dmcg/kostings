package com.oneeyedmen.kostings

import java.io.File


fun main(args: Array<String>) {
    if (args.isNotEmpty())
        patterns = args.toList()

    results.plot(imagesDir)
    val testClasses = results.allResults.toBenchmarkClasses().toTypedArray()
    val testResult = runTests(*testClasses)
    System.exit(if (testResult.wasSuccessful()) 0 else 1)
}

private val defaultPatterns = listOf("baselines", "strings", "primitives", "let")

private var patterns = defaultPatterns
private val baseOptions = Options("*", forks = 1, warmups = 10, measurements = 1000, discriminator = "run2")
private val resultsDir = File("results")
private val imagesDir = resultsDir.resolve("images").apply { mkdirs() }

val results by lazy {
    Results(patterns, baseOptions).apply {
        readOrRun(resultsDir)
    }
}

private fun Map<*, Result>.toBenchmarkClasses(): List<Class<*>> =
    values.map { it.benchmarkName.toClassName() }.toSet().map { Class.forName(it) }


private fun String.toClassName() = this.substringBeforeLast('.')

