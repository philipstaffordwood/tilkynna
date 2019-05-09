/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.retry;

public interface RetryPolicy {

    /**
     * Get the default maxAttempts server setting for retryCount (sent in application.yml), base on selected choice to retry or not.
     * 
     * @param doNotRetry
     *            indicates whether retry functionality is to be used or not.
     * @return default retry count when doNotRetry is false and zero when true
     */
    public short defaultRetryCount(boolean doNotRetry);

    /**
     * Given the currentRetryCount calculate what the next retryCount should be set to.
     * 
     * @param currentRetryCount
     * @return currentRetryCount decremented by 1, never lower than zero
     */
    public short calculateRetryCount(short currentRetryCount);

    /**
     * Given currentRetryCount is another retry needed or not?
     * 
     * @param currentRetryCount
     * @return
     */
    public boolean isRetryNeeded(short currentRetryCount);

    /**
     * delay in milliseconds to wait before retrying
     * 
     * @return backOffPeriod time in milliseconds
     */
    public int getBackOffPeriod();

}