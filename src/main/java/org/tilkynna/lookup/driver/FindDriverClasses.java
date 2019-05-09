/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.lookup.driver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FindDriverClasses {

    private FindDriverClasses() {
        // avoid instantiation of this class.. all methods are static
    }

    public static List<String> findClassesImplementing(Class<Driver> cls, String path) {
        URL urls[] = {};
        try (JarFileLoader cl = new JarFileLoader(urls);) {
            List<String> driverNames = new ArrayList<>();

            File folder = new File(path);
            for (File file : findFiles(folder, ".+\\.jar$")) {
                cl.addFile(file.getAbsolutePath());

                JarFile jarFile = new JarFile(file);
                for (JarEntry jarEntry : Collections.list(jarFile.entries())) {
                    String name = jarEntry.getName();
                    if (name.endsWith(".class")) {
                        try {
                            Class<?> found = cl.loadClass(name.replace("/", ".").replaceAll("\\.class$", ""));
                            if (cls.isAssignableFrom(found)) {
                                driverNames.add(name.replace("/", ".").replaceAll("\\.class$", ""));
                            }
                        } catch (Throwable ignore) {
                            // No real class file, or JAR not in classpath, or missing links.

                        }
                    }
                }

                jarFile.close();
            }

            return driverNames;
        } catch (IOException e) {
            throw new DriversLocationException("something went wrong loading drivers");
        }
    }

    private static List<File> findFiles(File directory, final String pattern) {
        File[] files = directory.listFiles((f, name) -> name.matches(pattern));
        return Arrays.asList(files);
    }

}