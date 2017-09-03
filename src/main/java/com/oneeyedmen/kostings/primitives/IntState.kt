package com.oneeyedmen.kostings.primitives

import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import java.util.*

@State(Scope.Thread)
open class IntState {

    val _41 = 41
    val nullable_41: Int? = _41
    val nullInt: Int? = null
    var randomInt = 0
    var randomNullableInt: Int? = 0

    @Setup(Level.Invocation)
    fun init() {
        randomInt = random.nextInt(10)
        randomNullableInt = if (randomInt < 5) randomInt else null
    }

    var result: Int = 0
}

private val random = Random()

