/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.integration;

import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.UUID;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import lombok.Getter;
import lombok.Setter;

//based on https://github.com/spring-projects/spring-integration-samples/blob/master/advanced/dynamic-ftp/src/main/java/org/springframework/integration/samples/ftp/DynamicFtpChannelResolver.java
// and http://scottfrederick.cfapps.io/blog/2012/05/22/Custom-PropertySource-in-Spring-3.1---Part-1
@Component
public class DynamicSftpChannelResolver {

    @Setter
    @Getter
    public class Channel {
        private MessageChannel messageChannel;
        private ZonedDateTime updatedOn;

        public Channel(MessageChannel messageChannel, ZonedDateTime updatedOn) {
            this.messageChannel = messageChannel;
            this.updatedOn = updatedOn;
        }

    }

    private final LinkedHashMap<UUID, Channel> channels = new LinkedHashMap<>();

    /**
     * Resolve destination to a channel, where each destination gets a private application context and the channel is the inbound channel to that application context.
     *
     * @param destination
     * @return a channel
     */
    public MessageChannel resolve(SFTPConfigSettings destination) {
        Channel channel = createOrUpdateDestinationChannel(destination);
        return channel.getMessageChannel();
    }

    @SuppressWarnings("resource")
    private synchronized Channel createOrUpdateDestinationChannel(SFTPConfigSettings destination) {
        // Check if the SFTP settings have been changed since context for channel was setup, if yes new need to reload it
        Channel channel = this.channels.get(destination.getDestinationId());
        if (channel == null || destination.getUpdatedOn().isAfter(channel.getUpdatedOn())) {
            ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "classpath:dynamic-sftp-outbound-adapter-context.xml" }, false);
            ctx.getEnvironment().getPropertySources().addFirst(getPropertySource(destination));
            ctx.refresh();

            MessageChannel messageChannel = ctx.getBean("toSftpChannel", MessageChannel.class);
            channel = new Channel(messageChannel, destination.getUpdatedOn());

            this.channels.put(destination.getDestinationId(), channel);
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
