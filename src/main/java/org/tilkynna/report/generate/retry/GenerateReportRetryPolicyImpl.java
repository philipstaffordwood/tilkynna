/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.retry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tilkynna.report.generate.config.GenerateReportRetryPolicyConfig;

@Component
public class GenerateReportRetryPolicyImpl implements RetryPolicy {

    @Autowired
    private GenerateReportRetryPolicyConfig retryConfig;

    @Override
    public short defaultRetryCount(boolean doNotRetry) {
        if (doNotRetry) {
            return 0;
        }
        return retryConfig.getMaxAttempts();
    }

    @Override
    public short calculateRetryCount(short currentRetryCount) {
        return (short) ((currentRetryCount == 0) ? 0 : currentRetryCount - 1);
    }

    @Override
    public boolean isRetryNeeded(short currentRetryCount) {
        return (currentRetryCount != 0);
    }

    @Override
    public int getBackOffPeriod() {
        return retryConfig.getBackOffPeriod();
    }
}
