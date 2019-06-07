/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

//help on how to mock out InetAddress found at: http://arinadevinfo.blogspot.com/2015/09/how-to-mock-static-methods-during-unit.html
@RunWith(PowerMockRunner.class)
public class ApplicationInstanceTests {

    @Test
    @PrepareForTest(ApplicationInstance.class)
    public void whenUnknownHostExceptionApplicationInstanceName_Unknown() throws Exception {
        PowerMockito.mockStatic(InetAddress.class);
        PowerMockito.when(InetAddress.getLocalHost()).then(new ThrowsException(new UnknownHostException()));
        String name = ApplicationInstance.name();

        assertNotNull("name should never be null", name);
        assertEquals("When UnknownHostException thrown name should be: 'Unknown'", ApplicationInstance.UNKNOWN_HOST, name);
    }

    @Test
    @PrepareForTest(ApplicationInstance.class)
    public void whenNoUnknownHostExceptionApplicationInstanceName_GivenName() throws Exception {
        PowerMockito.mockStatic(InetAddress.class);
        PowerMockito.when(InetAddress.getLocalHost().getHostName()).thenReturn("melissap");
        String name = ApplicationInstance.name();

        assertNotNull("name should never be null", name);
        assertEquals("name should be", "melissap", name);
    }

}
