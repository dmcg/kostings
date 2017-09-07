package com.oneeyedmen.kostings

import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import java.io.File

class Results(private val patterns: List<String>, private val baseOptions: Options) {

    lateinit var batches: List<Batch>

    val allResults by lazy {
        batches.flatMap { it.results }.associateBy { it.benchmarkName }
    }

    fun readOrRun(outputDir: File) {
        batches = patterns.map { readOrRunBenchmark(outputDir, baseOptions.copy(pattern = it)) }
    }

    fun resultNamed(name: String): Result? = allResults[name]

}

fun readOrRunBenchmark(outputDir: File, options: Options): Batch {
    val file = outputDir.resolve(options.outputFilename + ".csv")
    if (!file.isFile)
        runBenchmark(options, file)
    return readBatch(options, file)
}

private fun runBenchmark(options: Options, outputFile: File) {
    outputFile.parentFile.mkdirs()
    val optionsWithOutput = options.toOptions().result(outputFile.absolutePath).resultFormat(ResultFormatType.CSV).build()
    Runner(optionsWithOutput).run()
}