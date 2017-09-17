package com.oneeyedmen.kostings

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import org.junit.internal.RealSystem
import org.junit.internal.TextListener
import org.junit.runner.JUnitCore
import kotlin.jvm.internal.FunctionReference
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction2
import kotlin.reflect.jvm.jvmName

object Testing {
    @JvmStatic
    fun main(args: Array<String>) {

        val allResults = resurrectedBatches.batches.flatMap { it.results }
        val testClasses = allResults.toBenchmarkClasses().toTypedArray()
        val testResult = runTests(*testClasses)
        System.exit(if (testResult.wasSuccessful()) 0 else 1)
    }
}

object resurrectedBatches {
    val batches: List<Batch> = readBatches(canonicalResultsDir)

    fun resultNamed(benchmarkName: String): Result? = resultsByName[benchmarkName]

    private val resultsByName: Map<String, Result> = batches
        .flatMap { it.results }
        .groupBy { it.benchmarkName }
        .mapValues { entry -> entry.value.first() }

}

private fun List<Result>.toBenchmarkClasses(): List<Class<*>> =
    map { it.benchmarkName.toClassName() }.toSet().map { Class.forName(it) }

private fun String.toClassName() = this.substringBeforeLast('.')

fun runTests(vararg testClasses: Class<*>): org.junit.runner.Result =
    JUnitCore().apply {
        addListener(TextListener(RealSystem()))
    }.run(*testClasses)


fun meanIsFasterThan(benchmarkFunction: KFunction<*>) = benchmarkMatcher(Result::meanIsFasterThan, benchmarkFunction)
fun probablyFasterThan(benchmarkFunction: KFunction<*>) = benchmarkMatcher(Result::probablyFasterThan, benchmarkFunction)
fun probablySlowerThan(benchmarkFunction: KFunction<*>) = benchmarkMatcher(Result::probablySlowerThan, benchmarkFunction)
fun possiblyFasterThan(benchmarkFunction: KFunction<*>) = benchmarkMatcher(Result::possiblyFasterThan, benchmarkFunction)
fun possiblySlowerThan(benchmarkFunction: KFunction<*>) = benchmarkMatcher(Result::possiblySlowerThan, benchmarkFunction)
fun fasterByLessThan(benchmarkFunction: KFunction<*>, proportion: Double) = benchmarkMatcher(_fasterByLessThan(proportion), benchmarkFunction)

typealias ResultComparator = KFunction2<Result, Result, Boolean>

fun benchmarkMatcher(comparator: ResultComparator, benchmarkFunction: KFunction<*>): Matcher<KFunction<*>> =
    object : Matcher<KFunction<*>> {
        override val description get() = "with a benchmark result with ${comparator.name} faster than ${resultFor(benchmarkFunction)?.summary()}"

        override fun invoke(actual: KFunction<*>): MatchResult {
            val myResult = resultFor(benchmarkFunction)!!
            val actualResult = resultFor(actual)!!
            return if (comparator(actualResult, myResult)) {
                MatchResult.Match
            } else {
                MatchResult.Mismatch("was: ${actualResult.summary()}")
            }
        }
    }

fun _fasterByLessThan(proportion: Double): ResultComparator {
    fun fasterByLessThan(r1: Result, r2: Result) = r1.fasterByLessThan(r2, proportion)
    return ::fasterByLessThan
}

private fun resultFor(method: KFunction<*>) = method.methodName.let { resurrectedBatches.resultNamed(it) }

val KFunction<*>.methodName
    get() = (this as? FunctionReference)?.let {
        "${(it.owner as KClass<*>).jvmName}.${it.name}"
    } ?: throw IllegalArgumentException()
