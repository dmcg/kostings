package com.oneeyedmen.kostings

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.and
import com.oneeyedmen.kostings.matchers.Stats
import com.oneeyedmen.kostings.matchers.probablyDifferentTo
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

fun notProvablyFasterThan(benchmarkFunction: BenchmarkFunction) = probablyDifferentTo(benchmarkFunction) and ! probablyFasterThan(benchmarkFunction)

fun probablyFasterThan(benchmarkFunction: BenchmarkFunction, byMoreThan: Double, butNotMoreThan: Double, alpha: Double = 0.05) = object : Matcher<BenchmarkFunction>
{
    init {
        require(byMoreThan < butNotMoreThan) { "byMoreThan ($byMoreThan) >= butNotMoreThan ($butNotMoreThan)"}
    }

    override val description get() =
        "is probably faster that ${benchmarkFunction.methodName} by a factor of between $byMoreThan and $butNotMoreThan"

    override fun invoke(actual: BenchmarkFunction): MatchResult {
        val lowResult = probablyFasterThan(benchmarkFunction, byAFactorOf = byMoreThan, alpha = alpha).invoke(actual)
        if (lowResult is MatchResult.Mismatch)
            return MatchResult.Mismatch("actual was not faster by more than a factor of $byMoreThan because " + lowResult.description)

        val highResult = probablyFasterThan(benchmarkFunction, byAFactorOf = butNotMoreThan, alpha = alpha).invoke(actual)
        if (highResult is MatchResult.Match)
            return MatchResult.Mismatch("actual was probably faster by more than a factor of $butNotMoreThan")
        return MatchResult.Match
    }

}

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
