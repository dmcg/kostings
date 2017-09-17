package com.oneeyedmen.kostings

import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import java.io.File

class Batches(private val patterns: List<String>, private val baseOptions: BatchOptions) : Iterable<Batch> {

    lateinit var batches: List<Batch>

    val allResults by lazy {
        batches.flatMap { it.results }.associateBy { it.benchmarkName }
    }

    fun readOrRun(outputDir: File, resultFormatType: ResultFormatType) {
        batches = patterns.map { readOrRunBenchmark(outputDir, baseOptions.copy(pattern = it), resultFormatType) }
    }

    fun resultNamed(name: String): Result? = allResults[name]

    override fun iterator() = batches.iterator()

}

fun readOrRunBenchmark(outputDir: File, batchOptions: BatchOptions, resultFormatType: ResultFormatType): Batch {
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