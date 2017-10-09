package com.oneeyedmen.kostings

import org.openjdk.jmh.runner.options.CommandLineOptions


/**
 * Generates slides from [ResultSet]s formed from arbitrary benchmark patterns
 */
object Sliding {

    @JvmStatic
    fun main(args: Array<String>) {
        val commandLineOptions = CommandLineOptions(*args)
        val results = Results(Directories.canonicalResultsDir)

        val regexs = listOf("specialised_map_linkedList", "specialised_map_arrayList").map(String::toRegex)

        val matchingResults = results.allResults.filter { it.benchmarkName.matchesAny(regexs) }

        ArbitraryResultSet("thing", matchingResults).plotHistogramTo(Directories.canonicalResultsDir.resolve("thing"))
    }

}

private fun String.matchesAny(regexs: Iterable<Regex>) = regexs.find { it.find(this) != null } != null

class ArbitraryResultSet(
    override val description: String,
    override val results: List<Result>
) : ResultSet
