package com.oneeyedmen.kostings

import org.junit.runner.JUnitCore
import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.io.File


fun main(args: Array<String>) {

    val patterns = listOf("KotlinPrimitives")
    val options: ChainedOptionsBuilder = OptionsBuilder()
        .warmupIterations(1)
        .measurementIterations(1)
        .forks(1)


    val batches: List<Batch> = patterns.map { benchmark(options.resetPattern(it)) }

    batches.forEach {
        plot(it, File("${it.pattern}.png"))
    }

    val allResults: Map<String, Result> = batches.flatMap { it.results }.associateBy { it.benchmarkName }
    Results.allResults = allResults

    val result = JUnitCore.runClasses(*allResults.toBenchmarkClasses().toTypedArray())
    result.failures.forEach {
        println(it)
    }
    System.exit(if (result.wasSuccessful()) 0 else 1)
}

object Results {
    lateinit var allResults: Map<String, Result>

    fun resultNamed(name: String): Result? = allResults[name]
}

private fun Map<*, Result>.toBenchmarkClasses(): List<Class<*>> =
    values.map { it.benchmarkName.toClassName() }.toSet().map { Class.forName(it) }

fun benchmark(optionsBuilder: OptionsBuilder): Batch {
    val pattern = optionsBuilder.includes.first()
    val file = File.createTempFile(pattern, ".csv")
    val options = optionsBuilder.result(file.absolutePath).resultFormat(ResultFormatType.CSV).build()
    Runner(options).run()
    return readBatch(pattern, file)
}

private fun ChainedOptionsBuilder.resetPattern(pattern: String): OptionsBuilder = this.apply {
    (this as OptionsBuilder).includes.clear()
    include(pattern)
} as OptionsBuilder

private fun String.toClassName() = this.substringBeforeLast('.')




