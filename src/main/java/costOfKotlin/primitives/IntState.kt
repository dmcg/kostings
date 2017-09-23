package costOfKotlin.primitives

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
    var `50 50 NullableInt`: Int? = 0
    var `90 10 NullableInt`: Int? = 0

    @Setup(Level.Invocation)
    fun init() {
        randomInt = random.nextInt(10)
        `50 50 NullableInt` = if (randomInt < 5) null else randomInt
        `90 10 NullableInt` = if (randomInt < 1) null else randomInt
    }

    var result: Int = 0
}

private val random = Random()

