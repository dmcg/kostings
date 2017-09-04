package com.oneeyedmen.kostings.strings

import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Thread)
open class StringState {
    val hello = "hello"
    val world = "world"
}

