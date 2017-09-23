package com.oneeyedmen.kostings


object PlotRebuilding{

    @JvmStatic
    fun main(args: Array<String>) {
        readBatches(resultsDir).asIterable().plotIn(imagesDir)
    }

}

