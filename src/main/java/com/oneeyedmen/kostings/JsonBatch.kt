package com.oneeyedmen.kostings

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.io.IOException

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

fun readBatchFromJson(batchOptions: BatchOptions, jsonFile: File): Batch {
    val results = jacksonObjectMapper().readTree(jsonFile)?.asIterable()?.map { it.toResult() } ?: throw IOException("Can't read $jsonFile as JSON")
    return JsonBatch(batchOptions, jsonFile, results)
}

private fun JsonNode.toResult(): Result {
    val metricNode = this["primaryMetric"]
    val samples = metricNode["rawData"].asIterable().asIterable().flatten().map(JsonNode::asDouble).toDoubleArray()
        // TODO - not a very efficient way of reading
    return Result(
        benchmarkName = this["benchmark"].asText(),
        mode = this["mode"].asText(),
        _score = metricNode["score"].doubleValue(),
        error = metricNode["scoreError"].doubleValue(),
        units = metricNode["scoreUnit"].asText(),
        _samplesCount = samples.size,
        samples = samples
    )
}