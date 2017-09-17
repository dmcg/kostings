package com.oneeyedmen.kostings


object PlotRebuilding{

    @JvmStatic
    fun main(args: Array<String>) {
        val batches = readBatches(resultsDir)
        batches.plotHistograms(imagesDir)
        batches.plotSamples(imagesDir)
    }

}

