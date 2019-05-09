/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenerateReportRetryPolicyConfig {

    /**
     * The maximum number of times to retry a failed report generation
     */
    @Value("${tilkynna.generate.retry.maxAttempts}")
    short maxAttempts = 3;

    /**
     * The time in milliseconds to delay next retry since last failure
     */
    @Value("${tilkynna.generate.retry.backOffPeriodInMilliseconds:234}")
    short backOffPeriod = 5000;

    public short getMaxAttempts() {
        return maxAttempts;
    }

    public short getBackOffPeriod() {
        return backOffPeriod;
    }

    public void setMaxAttempts(short maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public void setBackOffPeriod(short backOffPeriod) {
        this.backOffPeriod = backOffPeriod;
    }
}
