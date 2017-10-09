package com.oneeyedmen.kostings

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import com.oneeyedmen.kostings.matchers.Stats
import com.oneeyedmen.kostings.matchers.probablyDifferentTo
import com.oneeyedmen.kostings.matchers.probablyLessThan
import com.oneeyedmen.kostings.matchers.probablyMoreThan
import org.junit.internal.RealSystem
import org.junit.internal.TextListener
import org.junit.runner.JUnitCore
import kotlin.jvm.internal.FunctionReference
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.jvmName

object Testing {
    @JvmStatic
    fun main(args: Array<String>) {
        val testClasses = ResurrectedBatches.allResults.toBenchmarkClasses().toTypedArray()
        val testResult = runTests(*testClasses)
        System.exit(if (testResult.wasSuccessful()) 0 else 1)
    }
}

object ResurrectedBatches {

    fun resultNamed(benchmarkName: String): CompositeResult? = resultsByName[benchmarkName]

    val allResults: Collection<CompositeResult> get() = resultsByName.values

    private val resultsByName: Map<String, CompositeResult> = readResults(canonicalResultsDir)
        .groupBy { it.benchmarkName }
        .mapValues { entry -> CompositeResult(entry.value) }

}

private fun Iterable<Result>.toBenchmarkClasses(): List<Class<*>> =
    map { it.benchmarkName.toClassName() }.toSet().map { Class.forName(it) }

private fun String.toClassName() = this.substringBeforeLast('.')

fun runTests(vararg testClasses: Class<*>): org.junit.runner.Result =
    JUnitCore().apply {
        addListener(TextListener(RealSystem()))
    }.run(*testClasses)


fun probablyDifferentTo(benchmarkFunction: KFunction<*>, alpha: Double = 0.05) =
    benchmarkMatcher(benchmarkFunction) { probablyDifferentTo(it, alpha) }

fun probablyFasterThan(benchmarkFunction: KFunction<*>, byAFactorOf: Double = 0.0, alpha: Double = 0.05) =
    benchmarkMatcher(benchmarkFunction) { probablyMoreThan(it, byAFactorOf, alpha) }

fun probablySlowerThan(benchmarkFunction: KFunction<*>, byAFactorOf: Double = 0.0, alpha: Double = 0.05) =
    benchmarkMatcher(benchmarkFunction) { probablyLessThan(it, byAFactorOf, alpha) }

private fun benchmarkMatcher(benchmarkFunction: KFunction<*>, comparator: (Stats) -> Matcher<Stats>) =
    object : Matcher<KFunction<*>> {
        override val description get() = delegateMatcher.description

        override fun invoke(actual: KFunction<*>): MatchResult {
            val actualResult = resultFor(actual)
            val matcher = delegateMatcher.invoke(actualResult)
            if (matcher is MatchResult.Mismatch) {
                return MatchResult.Mismatch(matcher.description+"\n(visual comparison can be found at "+ dumpComparison(actualResult,resultFor(benchmarkFunction))+" )")
            }
            else
                return matcher
        }

        private val delegateMatcher by lazy { comparator(resultFor(benchmarkFunction)) }
    }

private fun resultFor(method: KFunction<*>) =
    method.methodName.let { ResurrectedBatches.resultNamed(it) } ?: throw IllegalStateException("no results were found for ${method.methodName}")

val KFunction<*>.methodName
    get() = (this as? FunctionReference)?.let {
        "${(it.owner as KClass<*>).jvmName}.${it.name}"
    } ?: throw IllegalArgumentException()

private fun dumpComparison(result1: Result, result2: Result) : String {
    val template = Testing::class.java.getResource("/template.html")
    val renderJS = Testing::class.java.getResource("/render.js")
    val d3js = Testing::class.java.getResource("/d3.v4.min.js")
    var outputText = template.readText()
    outputText=outputText.replace("%%D3%%",d3js.path).replace("%%RENDER%%",renderJS.path)
    outputText=outputText.replace("%%TITLE%%", result1.benchmarkName+" vs "+ result2.benchmarkName).replace("%%GENERATED%%", Date().toString())

    outputText=outputText.replace("%%RESULT1%%", jacksonObjectMapper().writeValueAsString(result1.histogram()))
    outputText=outputText.replace("%%RESULT2%%",jacksonObjectMapper().writeValueAsString(result2.histogram()))
    outputText=outputText.replace("%%MEAN1%%", result1.stats.mean.toString())
    outputText=outputText.replace("%%MEAN2%%", result2.stats.mean.toString())

    outputText=outputText.replace("%%UNITS%%", result1.units)

    val file = createTempFile(suffix = ".html")
    file.writeText(outputText)
    return file.path
}

fun Result.histogram() : List<Array<Double>> {
    val bucketStats = EmpiricalDistribution(31)
    bucketStats.load(stats.values)
    return bucketStats.getBinStats().map { arrayOf(it.mean, it.n.toDouble()) }
}