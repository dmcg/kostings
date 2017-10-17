package com.oneeyedmen.kostings.matchers

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import org.apache.commons.math3.stat.descriptive.StatisticalSummary
import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues
import org.apache.commons.math3.stat.inference.TestUtils.tTest

/**
 * Is it likely that the actual [Stats] shows a significantly different mean metric than the reference [Stats] ? (either higher or lower)
 *
 * Use Student's T testing to determine if there is a statistically significant difference between the actual and reference samples in the [Stats]s
 *
 * @param[reference] the reference [Stats]
 * @param[alpha] the significance level, as a [Double] in percent
 *      (e.g. 0.05 represents an alpha of 5%, meaning there is a 5% chance this result is random - i.e. 95% probability that this conclusion is correct)
 *
 * @return a [com.natpryce.hamkrest.Matcher] class that can be used with [com.natpryce.hamkrest.assertion] assertions
 */
fun probablyDifferentTo(reference: Stats, alpha: Double = 0.05): Matcher<Stats> =
        object : Matcher<Stats> {
            override val description get() = "is statistically significantly different to ${reference.description}"

            override fun invoke(actual: Stats): MatchResult {
                //the null-Hypothesis is that actual == reference
                return matchIf(tTest(reference.data, actual.data), isLessThan = alpha)
            }
        }


/**
 * Is it likely that the actual [Stats] shows a significantly smaller metric than the reference [Stats] ?
 *
 * Use Student's T testing to determine if there is a statistically significant negative difference between the actual and reference samples in the [Stats]s
 *
 * @param[reference] the reference [Stats]
 * @param[byAFactorOf] the expected factor by which the actual mean is smaller than the reference mean, as a [Double] percent of the reference mean
 *      (e.g. 0.10 would be 10%, implying the actual mean is expected to be >10% smaller than the reference mean, or >90% of the reference mean)
 * @param[alpha] the significance level, as a [Double] in percent
 *      (e.g. 0.05 represents an alpha of 5%, meaning there is a 5% chance this result is random - i.e. 95% probability that this conclusion is correct)
 *
 * @return a [com.natpryce.hamkrest.Matcher] class that can be used with [com.natpryce.hamkrest.assertion] assertions
 */
fun probablyLessThan(reference: Stats, byAFactorOf: Double = 0.0, alpha: Double = 0.05): Matcher<Stats> =
        object : Matcher<Stats> {
            override val description get() = "is statistically significantly less than ${reference.description}" + descriptionOf(byAFactorOf)

            override fun invoke(actual: Stats): MatchResult {
                val referenceMean = reference.data.mean
                val factorAmount = referenceMean * byAFactorOf
                val offsetStats = actual.data.offsetBy(factorAmount)
                return if (offsetStats.mean >= referenceMean)
                    // no chance this is probable
                    meanMismatch(offsetStats.mean, factorAmount, ">=", referenceMean)
                else
                    // the null-Hypothesis is that actual mean-byAFactorOf*reference mean > reference mean
                    matchIf(oneSidedTTest(reference.data, offsetStats), isLessThan = alpha)
            }
        }


/**
 * Is it likely that the actual [Stats] shows a significantly greater metric than the reference [Stats] ?
 *
 * Use Student's T testing to determine if there is a statistically significant positive difference between the actual and reference samples in the [Stats]s
 *
 * @param[reference] the reference [Stats]
 * @param[byAFactorOf] the expected factor by which the actual mean is greater than the reference mean, as a [Double] percent of the reference mean
 *      (e.g. 0.10 would be 10%, implying the actual mean is expected to be >10% larger than the reference mean, or >110% of the reference mean)
 * @param[alpha] the significance level, as a [Double] in percent
 *      (e.g. 0.05 represents an alpha of 5%, meaning there is a 5% chance this result is random - i.e. 95% probability that this conclusion is correct)
 *
 * @return a [com.natpryce.hamkrest.Matcher] class that can be used with [com.natpryce.hamkrest.assertion] assertions
 */
fun probablyMoreThan(reference: Stats, byAFactorOf: Double = 0.0, alpha: Double = 0.05): Matcher<Stats> =
        object : Matcher<Stats> {
            override val description get() = "is statistically significantly more than ${reference.description}" + descriptionOf(byAFactorOf)

            override fun invoke(actual: Stats): MatchResult {
                val referenceMean = reference.data.mean
                val factorAmount = referenceMean * -byAFactorOf
                val offsetStats = actual.data.offsetBy(factorAmount)
                return if (offsetStats.mean <= referenceMean)
                    // no chance this is probable
                    meanMismatch(offsetStats.mean, factorAmount, "<=", referenceMean)
                else
                    // the null-Hypothesis is that actual mean-byAFactorOf*reference mean < reference mean
                    matchIf(oneSidedTTest(reference.data, offsetStats), isLessThan = alpha)
            }
        }

private fun oneSidedTTest(sampleStats1: StatisticalSummary, sampleStats2: StatisticalSummary) = tTest(sampleStats1, sampleStats2) / 2

private fun descriptionOf(byAFactorOf: Double) = if (byAFactorOf > 0.0) " by a factor of $byAFactorOf" else ""

private fun meanMismatch(offsetMean: Double, factorAmount: Double, comparison: String, referenceMean: Double) =
    MatchResult.Mismatch("[actual mean offset by $factorAmount (${offsetMean})] $comparison [reference mean ($referenceMean)] ")

private fun matchIf(actualAlpha: Double, isLessThan: Double) =
    if (actualAlpha < isLessThan) {
        // we can say actual mean != reference mean, with a probability of alpha% that we are wrong (i.e. (1 - alpha)% confidence)
        MatchResult.Match
    } else {
        // we can't say it's different
        MatchResult.Mismatch("[probability of being wrong ($actualAlpha)] >= [required probability ($isLessThan)]")
    }


private fun StatisticalSummary.offsetBy(offset: Double) = StatisticalSummaryValues(
    mean + offset,
    variance,
    n,
    max + offset,
    min + offset,
    sum + n * offset
)