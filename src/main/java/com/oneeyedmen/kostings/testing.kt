package com.oneeyedmen.kostings

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import com.oneeyedmen.kostings.matchers.Stats
import com.oneeyedmen.kostings.matchers.probablyDifferentTo
import com.oneeyedmen.kostings.matchers.probablyLessThan
import com.oneeyedmen.kostings.matchers.probablyMoreThan
import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.random.EmpiricalDistribution
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import java.util.*
import kotlin.jvm.internal.FunctionReference
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.jvmName

val resurrectedBatches by lazy { Results(Directories.canonicalResultsDir) }

fun probablyDifferentTo(benchmarkFunction: KFunction<*>, alpha: Double = 0.05) =
    benchmarkMatcher(benchmarkFunction, alpha) { probablyDifferentTo(it, alpha) }

fun probablyFasterThan(benchmarkFunction: KFunction<*>, byAFactorOf: Double = 0.0, alpha: Double = 0.05) =
    benchmarkMatcher(benchmarkFunction, alpha) { probablyMoreThan(it, byAFactorOf, alpha) }

fun probablySlowerThan(benchmarkFunction: KFunction<*>, byAFactorOf: Double = 0.0, alpha: Double = 0.05) =
    benchmarkMatcher(benchmarkFunction, alpha) { probablyLessThan(it, byAFactorOf, alpha) }

private fun benchmarkMatcher(benchmarkFunction: KFunction<*>, alpha : Double, comparator: (Stats) -> Matcher<Stats>) =
    object : Matcher<KFunction<*>> {
        override val description get() = delegateMatcher.description

        override fun invoke(actual: KFunction<*>): MatchResult {
            val actualResult = resultFor(actual)
            val matcher = delegateMatcher.invoke(actualResult)
            if (matcher is MatchResult.Mismatch) {
                return MatchResult.Mismatch(matcher.description+"\n(visual comparison can be found at "+ dumpComparison(actualResult,resultFor(benchmarkFunction), alpha)+" )")
            }
            else
                return matcher
        }

        private val delegateMatcher by lazy { comparator(resultFor(benchmarkFunction)) }
    }

private fun resultFor(method: KFunction<*>) =
    method.methodName.let { resurrectedBatches.resultNamed(it) } ?: throw IllegalStateException("no results were found for ${method.methodName}")

val KFunction<*>.methodName
    get() = (this as? FunctionReference)?.let {
        "${(it.owner as KClass<*>).jvmName}.${it.name}"
    } ?: throw IllegalArgumentException()

private fun dumpComparison(result1: Result, result2: Result, alpha: Double = 0.05) : String {
    val template = getResource("/template.html")
    val renderJS = getResource("/render.js")
    val d3js = getResource("/d3.v4.min.js")
    val stylesheet = getResource("/stylesheet.css")
    var outputText = template.readText()
    outputText=outputText.replace("%%D3%%",d3js.path).replace("%%RENDER%%",renderJS.path).replace("%%STYLESHEET%%",stylesheet.path)
    outputText=outputText.replace("%%RESULT1_NAME%%", result1.benchmarkName)
    outputText=outputText.replace("%%RESULT2_NAME%%", result2.benchmarkName)
    outputText=outputText.replace("%%GENERATED%%", Date().toString())

    outputText=outputText.replace("%%RESULT1%%", jacksonObjectMapper().writeValueAsString(result1.histogram()))
    outputText=outputText.replace("%%RESULT2%%",jacksonObjectMapper().writeValueAsString(result2.histogram()))
    outputText=outputText.replace("%%MEAN1%%", result1.data.mean.toString())
    outputText=outputText.replace("%%MEAN2%%", result2.data.mean.toString())
    outputText=outputText.replace("%%LOWERCI1%%", (result1.data.mean-result1.confidenceInterval(alpha)).toString())
    outputText=outputText.replace("%%UPPERCI1%%", (result1.data.mean+result1.confidenceInterval(alpha)).toString())
    outputText=outputText.replace("%%LOWERCI2%%", (result2.data.mean-result2.confidenceInterval(alpha)).toString())
    outputText=outputText.replace("%%UPPERCI2%%", (result2.data.mean+result2.confidenceInterval(alpha)).toString())

    outputText=outputText.replace("%%UNITS%%", result1.units)
    outputText=outputText.replace("%%ALPHA%%", alpha.toString())
    val meanDiffCI =meanDiffCI(result1.data,result2.data,alpha)
    val meanDiff = result2.data.mean-result1.data.mean
    val lowerMeanCI = meanDiff - meanDiffCI
    outputText=outputText.replace("%%DIFFLOWERCI%%", lowerMeanCI.toString())
    val upperMeanCI = meanDiff + meanDiffCI
    outputText=outputText.replace("%%DIFFUPPERCI%%", upperMeanCI.toString())

    val file = createTempFile(suffix = ".html")
    file.writeText(outputText)
    return file.path
}

private fun getResource(path: String) = object {}.javaClass.getResource(path)

fun Result.histogram() : List<Array<Double>> {
    val binCount = 21
    val bucketStats = EmpiricalDistribution(binCount)
    bucketStats.load(data.values)
    val upperBounds = bucketStats.upperBounds
    val min = data.values.min()!!
    var lastBound = min
    val result = mutableListOf<Array<Double>>()
    for ( (index, summaryStatistics) in bucketStats.getBinStats().asIterable().withIndex()) {
        val centre = ((upperBounds[index]-lastBound) /2.0) + lastBound
        lastBound = upperBounds[index]
        result.add(arrayOf(centre,summaryStatistics.n.toDouble()))
    }
    return result
}

fun Result.confidenceInterval(alpha: Double = 0.05) : Double {
    val tDist = TDistribution(data.n - 1.0)
    val a = tDist.inverseCumulativeProbability(1.0 - alpha / 2)
    return a * data.standardDeviation / Math.sqrt(data.n.toDouble())
}

private fun meanDiffCI(stats1 : DescriptiveStatistics, stats2 : DescriptiveStatistics, alpha: Double = 0.05): Double {
    val v1 = stats1.variance
    val n1 = stats1.n
    val v2 = stats2.variance
    val n2 =stats2.n
    val df = (v1 / n1 + v2 / n2) * (v1 / n1 + v2 / n2) / (v1 * v1 / (n1 * n1 * (n1 - 1.0)) + v2 * v2 / (n2 * n2 * (n2 - 1.0)))
    val tDist = TDistribution(df-1)
    val t = tDist.inverseCumulativeProbability(1.0 - alpha / 2)
    val mse = Math.sqrt( v1/n1 + v2/n2)
    val ci = t*mse
    return ci
}