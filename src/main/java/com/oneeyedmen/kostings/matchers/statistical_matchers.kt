package com.oneeyedmen.kostings.matchers

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import org.apache.commons.math3.stat.descriptive.StatisticalSummary
import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues
import org.apache.commons.math3.stat.inference.TestUtils.tTest

/**
 * Is it likely that the test [Stats] shows a significantly different mean metric than the benchmark [Stats] ? (either higher or lower)
 *
 * Use Student's T testing to determine if there is a statistically significant difference between the test and benchmark samples in the [Stats]s
 *
 * @param[reference] the benchmark [Stats]
 * @param[alpha] the significance level, as a [Double] in percent
 *      (e.g. 0.05 represents an alpha of 5%, meaning there is a 5% chance this result is random - i.e. 95% probability that this conclusion is correct)
 *
 * @return a [com.natpryce.hamkrest.Matcher] class that can be used with [com.natpryce.hamkrest.assertion] assertions
 */
fun probablyDifferentTo(reference: Stats, alpha: Double = 0.05): Matcher<Stats> =
        object : Matcher<Stats> {
            override val description get() = "was statistically significantly different to ${reference.description}"

            override fun invoke(actual: Stats): MatchResult {
                //the null-Hypothesis is that test == benchmark
                val actualAlpha = tTest(reference.data, actual.data)
                return matchIf(actualAlpha, isLessThan = alpha)
            }
        }


/**
 * Is it likely that the test [Stats] shows a significantly smaller metric than the benchmark [Stats] ?
 *
 * Use Student's T testing to determine if there is a statistically significant negative difference between the test and benchmark samples in the [Stats]s
 *
 * @param[reference] the benchmark [Stats]
 * @param[byAFactorOf] the expected factor by which the test mean is smaller than the benchmark mean, as a [Double] percent of the benchmark mean
 *      (e.g. 0.10 would be 10%, implying the test mean is expected to be >10% smaller than the benchmark mean, or >90% of the benchmark mean)
 * @param[alpha] the significance level, as a [Double] in percent
 *      (e.g. 0.05 represents an alpha of 5%, meaning there is a 5% chance this result is random - i.e. 95% probability that this conclusion is correct)
 *
 * @return a [com.natpryce.hamkrest.Matcher] class that can be used with [com.natpryce.hamkrest.assertion] assertions
 */
fun probablyLessThan(reference: Stats, byAFactorOf: Double = 0.0, alpha: Double = 0.05): Matcher<Stats> =
        object : Matcher<Stats> {
            override val description get() = "was statistically significantly less than ${reference.description}" + descriptionOf(byAFactorOf)

            override fun invoke(actual: Stats): MatchResult {
                val benchmarkMean = reference.data.mean
                val factorAmount = benchmarkMean * byAFactorOf
                val offsetStats = actual.data.offsetBy(factorAmount)
                //is the test mean actually larger?
                if (offsetStats.mean >= benchmarkMean)
                    //yup - so no chance this is probable
                    return MatchResult.Mismatch((if (byAFactorOf>0.0) "the offset mean was larger: offset" else "the mean was larger:") + " test mean[${offsetStats.mean}] > benchmark mean[$benchmarkMean]")
                else {
                    //t-Test the null-Hypothesis is that test mean-byAFactorOf*benchmark mean > benchmark mean
                    val actualAlpha = tTest(reference.data, offsetStats) / 2 //one-sided tTest so /2
                    return matchIf(actualAlpha, isLessThan = alpha)
                }
            }
        }


/**
 * Is it likely that the test [Stats] shows a significantly greater metric than the benchmark [Stats] ?
 *
 * Use Student's T testing to determine if there is a statistically significant positive difference between the test and benchmark samples in the [Stats]s
 *
 * @param[reference] the benchmark [Stats]
 * @param[byAFactorOf] the expected factor by which the test mean is greater than the benchmark mean, as a [Double] percent of the benchmark mean
 *      (e.g. 0.10 would be 10%, implying the test mean is expected to be >10% larger than the benchmark mean, or >110% of the benchmark mean)
 * @param[alpha] the significance level, as a [Double] in percent
 *      (e.g. 0.05 represents an alpha of 5%, meaning there is a 5% chance this result is random - i.e. 95% probability that this conclusion is correct)
 *
 * @return a [com.natpryce.hamkrest.Matcher] class that can be used with [com.natpryce.hamkrest.assertion] assertions
 */
fun probablyMoreThan(reference: Stats, byAFactorOf: Double = 0.0, alpha: Double = 0.05): Matcher<Stats> =
        object : Matcher<Stats> {
            override val description get() = "was statistically significantly more than ${reference.description}" + descriptionOf(byAFactorOf)

            override fun invoke(actual: Stats): MatchResult {
                val benchmarkMean = reference.data.mean
                val factorAmount = benchmarkMean * byAFactorOf
                val offsetStats = actual.data.offsetBy(-factorAmount)
                //is the test mean actually smaller?
                if (offsetStats.mean <= benchmarkMean)
                    //yup - so no chance this is probable
                    return MatchResult.Mismatch((if (byAFactorOf>0.0) "the offset mean was smaller: offset" else "the mean was smaller:") + " test mean[${offsetStats.mean}] < benchmark mean[$benchmarkMean]")
                else {
                    //t-Test the null-Hypothesis is that test mean-byAFactorOf*benchmark mean < benchmark mean
                    val actualAlpha = tTest(reference.data, offsetStats) / 2 //one-sided tTest so /2
                    return matchIf(actualAlpha, isLessThan = alpha)
                }
            }
        }

private fun descriptionOf(byAFactorOf: Double) = if (byAFactorOf > 0.0) " by a factor of $byAFactorOf" else ""

private fun matchIf(actualAlpha: Double, isLessThan: Double) =
    if (actualAlpha < isLessThan) {
        // we can say test mean != benchmark mean, with a probability of alpha% that we are wrong (i.e. (1 - alpha)% confidence)
        MatchResult.Match
    } else {
        // we can't say it's different
        MatchResult.Mismatch("the expectation cannot be met because the probability of being wrong $actualAlpha is greater than required probability alpha[$isLessThan]")
    }


private fun StatisticalSummary.offsetBy(offset: Double) = StatisticalSummaryValues(
    mean + offset,
    variance,
    n,
    max + offset,
    min + offset,
    sum + n * offset
)