package com.oneeyedmen.kostings

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import com.oneeyedmen.kostings.matchers.Stats
import com.oneeyedmen.kostings.matchers.probablyDifferentTo
import com.oneeyedmen.kostings.matchers.probablyLessThan
import com.oneeyedmen.kostings.matchers.probablyMoreThan
import kotlin.jvm.internal.FunctionReference
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.jvmName

val resurrectedBatches by lazy { Results(Directories.canonicalResultsDir) }

typealias BenchmarkFunction = KFunction<*>

fun probablyDifferentTo(benchmarkFunction: BenchmarkFunction, alpha: Double = 0.05) =
    benchmarkMatcher(benchmarkFunction, alpha) { probablyDifferentTo(it, alpha) }

fun probablyFasterThan(benchmarkFunction: BenchmarkFunction, byAFactorOf: Double = 0.0, alpha: Double = 0.05) =
    benchmarkMatcher(benchmarkFunction, alpha) { probablyMoreThan(it, byAFactorOf, alpha) }

fun probablySlowerThan(benchmarkFunction: BenchmarkFunction, byAFactorOf: Double = 0.0, alpha: Double = 0.05) =
    benchmarkMatcher(benchmarkFunction, alpha) { probablyLessThan(it, byAFactorOf, alpha) }

private fun benchmarkMatcher(benchmarkFunction: BenchmarkFunction, alpha : Double, comparator: (Stats) -> Matcher<Stats>) =
    object : Matcher<BenchmarkFunction> {
        override val description get() = delegateMatcher.description

        override fun invoke(actual: BenchmarkFunction): MatchResult {
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

private fun resultFor(method: BenchmarkFunction) =
    method.methodName.let { resurrectedBatches.resultNamed(it) } ?: throw IllegalStateException("no results were found for ${method.methodName}")

val BenchmarkFunction.methodName
    get() = (this as? FunctionReference)?.let {
        "${(it.owner as KClass<*>).jvmName}.${it.name}"
    } ?: throw IllegalArgumentException()
