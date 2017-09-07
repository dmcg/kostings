package com.oneeyedmen.kostings.primitives

import com.oneeyedmen.kostings.Result
import com.oneeyedmen.kostings.check
import org.junit.Test
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.infra.Blackhole


open class KotlinPrimitives {

    @Benchmark
    fun _1_baseline(state: IntState, blackhole: Blackhole) {
        /* Needs a non-null check on the parameter compared to the Java version. This is not detectable.
         */
        blackhole.consume(state._41)
    }
//      public final _1_baseline(Lcom/oneeyedmen/kostings/primitives/IntState;Lorg/openjdk/jmh/infra/Blackhole;)V
//    @Lorg/openjdk/jmh/annotations/Benchmark;()
//    @Lorg/jetbrains/annotations/NotNull;() // invisible, parameter 0
//    @Lorg/jetbrains/annotations/NotNull;() // invisible, parameter 1
//    L0
//    ALOAD 1
//    LDC "state"
//    INVOKESTATIC kotlin/jvm/internal/Intrinsics.checkParameterIsNotNull (Ljava/lang/Object;Ljava/lang/String;)V
//    ALOAD 2
//    LDC "blackhole"
//    INVOKESTATIC kotlin/jvm/internal/Intrinsics.checkParameterIsNotNull (Ljava/lang/Object;Ljava/lang/String;)V
//    L1
//    LINENUMBER 16 L1
//    ALOAD 2
//    ALOAD 1
//    INVOKEVIRTUAL com/oneeyedmen/kostings/primitives/IntState.get_41 ()I
//    INVOKEVIRTUAL org/openjdk/jmh/infra/Blackhole.consume (I)V
//    L2
//    LINENUMBER 17 L2
//    RETURN
//    L3
//    LOCALVARIABLE this Lcom/oneeyedmen/kostings/primitives/KotlinPrimitives; L0 L3 0
//    LOCALVARIABLE state Lcom/oneeyedmen/kostings/primitives/IntState; L0 L3 1
//    LOCALVARIABLE blackhole Lorg/openjdk/jmh/infra/Blackhole; L0 L3 2
//    MAXSTACK = 2
//    MAXLOCALS = 3

    @Benchmark
    fun _2_sum(state: IntState, blackhole: Blackhole) {
        blackhole.consume(state._41 + 1)
    }

    @Benchmark
    fun _3_sum_nullable(state: IntState, blackhole: Blackhole) {
        /* Needs a null-check and Integer.intValue() compared to #sum_with_state. These are not detectable.
         */
        blackhole.consume(state.nullable_41!! + 1)
    }

    @Benchmark
    fun _4_sum_always_null(state: IntState, blackhole: Blackhole) {
        /* The other side of the coin. Still as fast.
        */
        blackhole.consume(state.nullInt ?: 0 + 1)
    }

    @Benchmark
    fun _5_sum_50_50_nullable(state: IntState, blackhole: Blackhole) {
        /* Stop always picking the same branch by forcing a random path. Mixed results.
        */
        blackhole.consume(state.`50 50 NullableInt` ?: 0 + 1)
    }

    @Benchmark
    fun _6_sum_90_10_nullable(state: IntState, blackhole: Blackhole) {
        /* Stop always picking the same branch by forcing a random path. Finally slower.
        */
        blackhole.consume(state.`90 10 NullableInt` ?: 0 + 1)
    }

    @Test
    fun `parameter null check is undetectable`() {
        check(XJavaPrimitives::_1_baseline, Result::couldBeSlowerThan, this::_1_baseline)
    }

    @Test
    fun `nothing is definitely faster than baseline`() {
        check(this::_1_baseline, Result::couldBeSlowerThan, this::_2_sum)
        check(this::_1_baseline, Result::couldBeSlowerThan, this::_3_sum_nullable)
        check(this::_1_baseline, Result::couldBeSlowerThan, this::_4_sum_always_null)
        check(this::_1_baseline, Result::couldBeSlowerThan, this::_5_sum_50_50_nullable)
        check(this::_1_baseline, Result::couldBeSlowerThan, this::_6_sum_90_10_nullable)
    }

    @Test
    fun `nullable is not detectably slower than non-nullable`() {
        check(this::_2_sum, Result::couldBeSlowerThan, this::_3_sum_nullable)
        check(this::_2_sum, Result::couldBeSlowerThan, this::_4_sum_always_null)
        check(this::_2_sum, Result::couldBeSlowerThan, this::_5_sum_50_50_nullable)
        check(this::_2_sum, Result::couldBeSlowerThan, this::_6_sum_90_10_nullable)
    }
}




/*
Summary - neither null-checks nor unboxing are detectable
*/

