/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * Factory bean for the instance of the {@link IReportEngine report engine}.
 * 
 * Taken from https://spring.io/blog/2012/01/30/spring-framework-birt
 */
public class BirtEngineFactory implements FactoryBean<IReportEngine>, DisposableBean {

    protected static final Log logger = LogFactory.getLog(BirtEngineFactory.class);

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Autowired
    private ApplicationContext context;

    private IReportEngine birtEngine;
    private File resolvedDirectory;
    private java.util.logging.Level logLevel = java.util.logging.Level.SEVERE;

    @Override
    public void destroy() throws Exception {
        birtEngine.destroy();
        Platform.shutdown();
    }

    public void setLogLevel(java.util.logging.Level ll) {
        this.logLevel = ll;
    }

    public void setLogDirectory(org.springframework.core.io.Resource resource) {
        File f = null;
        try {
            f = resource.getFile();
            validateLogDirectory(f);
            this.resolvedDirectory = f;
        } catch (IOException e) {
            throw new RuntimeException("Failed to set the LOG directory");
        }

    }

    private void validateLogDirectory(File f) {
        Assert.notNull(f, " the directory must not be null");
        Assert.isTrue(f.isDirectory(), " the path given must be a directory");
        Assert.isTrue(f.exists(), "the path specified must exist!");
    }

    public void setLogDirectory(java.io.File f) {
        validateLogDirectory(f);
        this.resolvedDirectory = f;
    }

    /**
     * Initialize BIRT Report Engine configuration.
     */
    @Override
    @SuppressWarnings("unchecked")
    public IReportEngine getObject() {
        EngineConfig config = new EngineConfig();

        // This line injects the Spring Context into the BIRT Context
        config.getAppContext().put("spring", this.context);

        config.setLogConfig(null != this.resolvedDirectory ? this.resolvedDirectory.getAbsolutePath() : null, this.logLevel);

        try {
            Platform.startup(config);
        } catch (BirtException e) {
            throw new RuntimeException("Could not start the Birt engine!", e);
        }

        IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        this.birtEngine = factory.createReportEngine(config);

        List<String> header = new ArrayList<String>();
        header.add("BirtEngineFactory Started");
        logger.info("******************************************************************");
        logger.info("********************    " + header + "    ************************");
        logger.info("******************************************************************");

        return birtEngine;
    }

    @Override
    public Class<?> getObjectType() {
        return IReportEngine.class;
    }

}
