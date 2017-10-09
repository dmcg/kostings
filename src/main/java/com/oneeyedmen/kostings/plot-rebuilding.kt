package com.oneeyedmen.kostings


object PlotRebuilding{

    @JvmStatic
    fun main(args: Array<String>) {
        readBatches(Directories.resultsDir).asIterable().plotIn(Directories.imagesDir)
    }

}

