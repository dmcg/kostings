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

fun runTests(vararg testClasses: Class<*>): org.junit.runner.Result =
    JUnitCore().apply {
        addListener(TextListener(RealSystem()))
    }.run(*testClasses)


fun meanIsFasterThan(benchmarkFunction: KFunction<*>) = benchmarkMatcher(Result::meanIsFasterThan, benchmarkFunction)
fun couldBeFasterThan(benchmarkFunction: KFunction<*>) = benchmarkMatcher(Result::couldBeFasterThan, benchmarkFunction)
fun couldBeSlowerThan(benchmarkFunction: KFunction<*>) = benchmarkMatcher(Result::couldBeSlowerThan, benchmarkFunction)

fun benchmarkMatcher(comparator: KFunction2<Result, Result, Boolean>, benchmarkFunction: KFunction<*>): Matcher<KFunction<*>> =
    object : Matcher<KFunction<*>> {
        override val description get() = "with a benchmark result with ${comparator.name} faster than ${resultFor(benchmarkFunction)}"

        override fun invoke(actual: KFunction<*>): MatchResult {
            val myResult = resultFor(benchmarkFunction)!!
            val actualResult = resultFor(actual)!!
            return if (comparator(actualResult, myResult)) {
                MatchResult.Match
            } else {
                MatchResult.Mismatch("was: $actualResult")
            }
        }
    }


private fun resultFor(method: KFunction<*>) = method.methodName.let { results.resultNamed(it) }

val KFunction<*>.methodName
    get() = (this as? FunctionReference)?.let {
        "${(it.owner as KClass<*>).jvmName}.${it.name}"
    } ?: throw IllegalArgumentException()
