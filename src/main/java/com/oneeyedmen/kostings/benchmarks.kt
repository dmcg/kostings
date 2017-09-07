package com.oneeyedmen.kostings

import java.io.File


fun main(args: Array<String>) {
    results.plot(outputDir)

    val testClasses = results.allResults.toBenchmarkClasses().toTypedArray()
    val testResult = runTests(*testClasses)
    System.exit(if (testResult.wasSuccessful()) 0 else 1)
}

private val patterns = listOf("strings", "primitives", "let", "noop")
private val baseOptions = Options("*", forks = 10, warmups = 5, measurements = 21)
private val outputDir = File("results")

val results by lazy {
    Results(patterns, baseOptions).apply {
        readOrRun(outputDir)
    }
}

private fun Map<*, Result>.toBenchmarkClasses(): List<Class<*>> =
    values.map { it.benchmarkName.toClassName() }.toSet().map { Class.forName(it) }


private fun String.toClassName() = this.substringBeforeLast('.')

