package com.oneeyedmen.kostings

import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.io.File


fun main(args: Array<String>) {

    val outputDir = File("results")
    val patterns = listOf("primitives")
    val baseOptions = Options("*", forks = 1, warmups = 5, measurements = 5)

    val batches: List<Batch> = patterns.map { readOrRunBenchmark(outputDir, baseOptions.copy(pattern = it)) }

    batches.forEach {
        plot(it, File("${it.pattern}.png"))
    }

    val allResults: Map<String, Result> = batches.flatMap { it.results }.associateBy { it.benchmarkName }
    Results.allResults = allResults

    val testClasses = allResults.toBenchmarkClasses().toTypedArray()

    val result = runTests(*testClasses)
    System.exit(if (result.wasSuccessful()) 0 else 1)
}

object Results {
    lateinit var allResults: Map<String, Result>

    fun resultNamed(name: String): Result? = allResults[name]
}

private fun Map<*, Result>.toBenchmarkClasses(): List<Class<*>> =
    values.map { it.benchmarkName.toClassName() }.toSet().map { Class.forName(it) }

fun readOrRunBenchmark(outputDir: File, options: Options): Batch {
    val file = outputDir.resolve(options.outputFilename + ".csv")
    if (!file.isFile)
        runBenchmark(options, file)
    return readBatch(options.pattern, file)
}

private fun runBenchmark(options: Options, outputFile: File) {
    outputFile.parentFile.mkdirs()
    val optionsWithOutput = options.toOptions().result(outputFile.absolutePath).resultFormat(ResultFormatType.CSV).build()
    Runner(optionsWithOutput).run()
}


private fun String.toClassName() = this.substringBeforeLast('.')

data class Options(val pattern: String, val forks: Int, val warmups: Int, val measurements: Int) {
    fun toOptions() = OptionsBuilder().include(pattern).forks(forks).warmupIterations(warmups).measurementIterations(measurements)
    val outputFilename: String get() = "$pattern-f$forks-w$warmups-m$measurements"
}


