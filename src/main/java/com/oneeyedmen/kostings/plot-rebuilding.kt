package com.oneeyedmen.kostings


object PlotRebuilding{

    @JvmStatic
    fun main(args: Array<String>) {
        val batches = readBatches()
        batches.plotHistograms(imagesDir)
        batches.plotSamples(imagesDir)
    }

}

