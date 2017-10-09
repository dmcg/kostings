package com.oneeyedmen.kostings

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
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

private fun benchmarkMatcher(benchmarkFunction: KFunction<*>, comparator: (PerformanceData) -> Matcher<PerformanceData>) =
    object : Matcher<KFunction<*>> {
        override val description get() = delegateMatcher.description

        override fun invoke(actual: KFunction<*>): MatchResult {
            val actualResult = resultFor(actual)
            return delegateMatcher.invoke(actualResult.performanceData)
        }

        private val delegateMatcher by lazy { comparator(resultFor(benchmarkFunction).performanceData) }
    }

private fun resultFor(method: KFunction<*>) =
    method.methodName.let { ResurrectedBatches.resultNamed(it) } ?: throw IllegalStateException("no results were found for ${method.methodName}")

val KFunction<*>.methodName
    get() = (this as? FunctionReference)?.let {
        "${(it.owner as KClass<*>).jvmName}.${it.name}"
    } ?: throw IllegalArgumentException()
