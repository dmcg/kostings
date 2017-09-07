package com.oneeyedmen.kostings;

import java.io.InputStream
import java.io.PrintStream
import java.io.Reader
import java.nio.charset.Charset

fun <T> T.printed(stream: PrintStream = System.out) = apply { stream.println(this) }
fun <T> Iterable<T>.printed(stream: PrintStream = System.out) = this.map { it.printed(stream) }

fun Reader.printed(stream: PrintStream = System.out, charset: Charset = Charsets.UTF_8) =
    readText().printed(stream).byteInputStream(charset)

fun InputStream.printed(stream: PrintStream = System.out, charset: Charset = Charsets.UTF_8) =
    bufferedReader(charset).printed(stream)

// set a breakpoint in me and wrap me around an expression
fun <T> breakpoint(thing: T): T {
    return thing
}
