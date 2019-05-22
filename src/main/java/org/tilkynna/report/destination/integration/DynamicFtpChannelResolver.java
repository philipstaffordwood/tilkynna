/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.integration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

//based on https://github.com/spring-projects/spring-integration-samples/blob/master/advanced/dynamic-ftp/src/main/java/org/springframework/integration/samples/ftp/DynamicFtpChannelResolver.java
// and http://scottfrederick.cfapps.io/blog/2012/05/22/Custom-PropertySource-in-Spring-3.1---Part-1
@Component
public class DynamicFtpChannelResolver {

    public static final int MAX_CACHE_SIZE = 50;

    private final LinkedHashMap<UUID, MessageChannel> channels = new LinkedHashMap<UUID, MessageChannel>() {

        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Entry<UUID, MessageChannel> eldest) {
            // This returning true means the least recently used
            // channel and its application context will be closed and removed
            boolean remove = size() > MAX_CACHE_SIZE;
            if (remove) {
                MessageChannel channel = eldest.getValue();
                ConfigurableApplicationContext ctx = contexts.get(channel);
                if (ctx != null) { // shouldn't be null ideally
                    ctx.close();
                    contexts.remove(channel);
                }
            }
            return remove;
        }
    };

    private final Map<MessageChannel, ConfigurableApplicationContext> contexts = new HashMap<>();

    /**
     * Resolve destination to a channel, where each destination gets a private application context and the channel is the inbound channel to that application context.
     *
     * @param destination
     * @return a channel
     */
    public MessageChannel resolve(SFTPConfigSettings destination) {
        // TODO need to check if the sftp settings have been changed since last added to channels array for destinationId
        // TODO if yes then we need to reload the context

        MessageChannel channel = this.channels.get(destination.getDestinationId());
        if (channel == null) {
            channel = createNewDestinationChannel(destination);
        }
        return channel;
    }

    private synchronized MessageChannel createNewDestinationChannel(SFTPConfigSettings destination) {
        MessageChannel channel = this.channels.get(destination.getDestinationId());
        if (channel == null) {
            ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "classpath:dynamic-sftp-outbound-adapter-context.xml" }, false);
            ctx.getEnvironment().getPropertySources().addFirst(getPropertySource(destination));
            ctx.refresh();
            channel = ctx.getBean("toSftpChannel", MessageChannel.class);

            this.channels.put(destination.getDestinationId(), channel);
            // Will works as the same reference is presented always
            this.contexts.put(channel, ctx);
        }

        return channel;
    }

    private PropertySource<?> getPropertySource(SFTPConfigSettings destination) {
        Properties props = new Properties();

        props.setProperty("remote.directory", destination.getWorkingDirectory() != null ? destination.getWorkingDirectory() : "/");
        props.setProperty("host", destination.getHost());
        props.setProperty("port", destination.getPort().toString());
        props.setProperty("user", destination.getUsername());
        props.setProperty("password", new String(destination.getPassword()));

        return new PropertiesPropertySource("ftpprops", props);
    }

    public boolean test(SFTPConfigSettings destination) {
        JSch jsch = new JSch();
        Session session = null;
        try {
            session = jsch.getSession(destination.getUsername(), destination.getHost(), destination.getPort());
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(new String(destination.getPassword()));
            session.connect();

        } catch (JSchException e) {
            return false;
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }

        return true;
    }
}
