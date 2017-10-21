package com.oneeyedmen.kostings


object PlotRebuilding{

    @JvmStatic
    fun main(args: Array<String>) {
        readBatches(Directories.canonicalResultsDir).asIterable().plotIn(Directories.canonicalResultsDir, rebuild = false)
    }

}

