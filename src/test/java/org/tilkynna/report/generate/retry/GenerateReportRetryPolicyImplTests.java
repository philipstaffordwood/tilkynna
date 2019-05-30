/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.retry;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.tilkynna.report.generate.processengine.config.GenerateReportRetryPolicyConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { GenerateReportRetryPolicyConfig.class, GenerateReportRetryPolicyImpl.class }, //
        initializers = ConfigFileApplicationContextInitializer.class)
public class GenerateReportRetryPolicyImplTests {

    @Autowired
    private RetryPolicy retryPolicy;

    @Test
    public void givenTrueDoNotRetry_when_retryCountIsZero() throws Exception {
        boolean doNotRetry = true;
        int defaultRetry = retryPolicy.defaultRetryCount(doNotRetry);

        Assert.assertEquals("defaultRetry expected to be zero", 0, defaultRetry);
    }

    @Test
    public void givenFalseDoNotRetry_when_retryCountIsNotZero() throws Exception {
        boolean doNotRetry = false;
        int defaultRetry = retryPolicy.defaultRetryCount(doNotRetry);

        Assert.assertEquals("defaultRetry is not 3", 3, defaultRetry);
        Assert.assertTrue("defaultRetry is not Zero", defaultRetry != 0);
    }

    @Test
    public void givenRetryCountZero_whenCalculateRetryCount_notDecremented() throws Exception {
        short retry = 0;
        short calculatedRetryCount = retryPolicy.calculateRetryCount(retry);

        Assert.assertEquals("calculatedRetryCount should be zero too", retry, calculatedRetryCount);
        Assert.assertFalse("calculatedRetryCount less than Zero", calculatedRetryCount < 0);
    }

    @Test
    public void givenRetryCountThree_whenCalculateRetryCount_decrementedByOne() throws Exception {
        short retry = 3;
        short calculatedRetryCount = retryPolicy.calculateRetryCount(retry);

        Assert.assertEquals("calculatedRetryCount should be 1 less", (retry - 1), calculatedRetryCount);
        Assert.assertFalse("calculatedRetryCount should not be same as original retry", retry == calculatedRetryCount);
    }

    @Test
    public void givenRetryCountZero_whenIsRetryNeeded_False() throws Exception {
        short retry = 0;
        boolean isRetryNeeded = retryPolicy.isRetryNeeded(retry);

        Assert.assertFalse("isRetryNeeded should be false", isRetryNeeded);
    }

    @Test
    public void givenRetryCountGreaterZero_whenIsRetryNeeded_True() throws Exception {
        short retry = 5;
        boolean isRetryNeeded = retryPolicy.isRetryNeeded(retry);

        Assert.assertTrue("isRetryNeeded should be true", isRetryNeeded);
    }
}
