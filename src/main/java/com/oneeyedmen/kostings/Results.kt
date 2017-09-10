package com.oneeyedmen.kostings

import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import java.io.File

class Results(private val patterns: List<String>, private val baseOptions: BatchOptions) {

    lateinit var batches: List<Batch>

    val allResults by lazy {
        batches.flatMap { it.results }.associateBy { it.benchmarkName }
    }

    fun readOrRun(outputDir: File) {
        batches = patterns.map { readOrRunBenchmark(outputDir, baseOptions.copy(pattern = it)) }
    }

    fun resultNamed(name: String): Result? = allResults[name]

}

fun readOrRunBenchmark(outputDir: File, batchOptions: BatchOptions): Batch {
    val file = outputDir.resolve(batchOptions.outputFilename + ".csv")
    if (!file.isFile)
        runBenchmark(batchOptions, file)
    return readBatch(batchOptions, file)
}

private fun runBenchmark(batchOptions: BatchOptions, outputFile: File) {
    outputFile.parentFile.mkdirs()
    val optionsWithOutput = batchOptions.toOptions().result(outputFile.absolutePath).resultFormat(ResultFormatType.CSV).build()
    Runner(optionsWithOutput).run()
}