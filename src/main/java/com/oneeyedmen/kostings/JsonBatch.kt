package com.oneeyedmen.kostings

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.math.BigDecimal

data class JsonBatch(
    override val batchOptions: BatchOptions,
    override val dataFile: File,
    override val results: List<Result>)
: Batch {

    override val summaryCsvFile: File by lazy {
        File.createTempFile(batchOptions.outputFilename, ".csv").apply {
            writeCSV(this)
        }
    }

    val samplesCsvFile: File by lazy {
        File.createTempFile(batchOptions.outputFilename, ".samples.csv").apply {
            writeSamplesCSV(this)
        }
    }
}

fun readBatchFromJson(batchOptions: BatchOptions, jsonFile: File): Batch =
    JsonBatch(batchOptions, jsonFile,
        jacksonObjectMapper().readTree(jsonFile).asIterable().map { it.toResult() }
    )

private fun JsonNode.toResult(): Result {
    val metricNode = this["primaryMetric"]
    val samples = metricNode["rawData"].asIterable().asIterable().flatten().map(JsonNode::toBigDecimal)
    return Result(
        benchmarkName = this["benchmark"].asText(),
        mode = this["mode"].asText(),
        score = metricNode["score"].toBigDecimal(),
        error = metricNode["scoreError"].toBigDecimal(),
        units = metricNode["scoreUnit"].asText(),
        samplesCount = samples.size,
        samples = samples
    )
}

private fun JsonNode.toBigDecimal() = BigDecimal(this.asText())