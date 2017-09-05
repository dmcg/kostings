package com.oneeyedmen.kostings

import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import org.junit.internal.RealSystem
import org.junit.internal.TextListener
import org.junit.runner.JUnitCore
import kotlin.jvm.internal.FunctionReference
import kotlin.reflect.KFunction
import kotlin.reflect.KFunction2

fun runTests(vararg testClasses: Class<*>): org.junit.runner.Result =
    JUnitCore().apply {
        addListener(TextListener(RealSystem()))
    }.run(*testClasses)


fun check(lhs: KFunction<*>, comparator: KFunction2<Result, Result, Boolean>, rhs: KFunction<*>) {
    val lhsResult = resultFor(lhs)!!
    val rhsResult = resultFor(rhs)!!
    assertThat(lhsResult, Matcher(comparator, rhsResult))
}

private fun resultFor(method: KFunction<*>) = method.methodName?.let { Results.resultNamed(it) }

val KFunction<*>.methodName get() = (this as? FunctionReference)?.let {
    "${it.boundReceiver.javaClass.name}.${it.name}"
}
