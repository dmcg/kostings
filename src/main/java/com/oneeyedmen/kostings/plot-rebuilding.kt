package com.oneeyedmen.kostings

import java.io.File


fun main(args: Array<String>) {
    val batches = resultsDir.listFiles { _, name -> name.endsWith(".json") }.mapNotNull { it.toBatch() }
    batches.plotHistograms(imagesDir)
    batches.plotSamples(imagesDir)
}

private fun File.toBatch(): Batch? {
    val batchOptions = this.toBatchOptions() ?: return null
    return readBatchFromJson(batchOptions, this)
}

private val filenameRegex = """^(.*?)-f(\d+)-w(\d+)-m(\d+)-(.*)\.json""".toRegex()

private fun File.toBatchOptions(): BatchOptions? {
    val name = name
    val match: MatchResult = filenameRegex.matchEntire(name) ?: return null

    return BatchOptions(
        pattern = match.value(1),
        forks = match.value(2).toInt(),
        warmups = match.value(3).toInt(),
        measurements = match.value(4).toInt(),
        discriminator = match.value(5))
}

private fun MatchResult.value(index: Int) = groups[index]!!.value

