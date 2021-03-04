package com.gitlab.hillel.dnepr.java.ee.common.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ResourceResolver {
    private final Path rootPath;

    public ResourceResolver() {
        this(Path.of(getDefaultResourceURI()));
    }

    public ResourceResolver(String rootPath) {
        this(Path.of(rootPath));
    }

    public ResourceResolver(Path rootPath) {
        this.rootPath = Path
                .of(rootPath.toUri())
                .toAbsolutePath()
                .normalize();
    }

    private static URI getDefaultResourceURI() {
        try {
            return ResourceResolver.class.getResource("/").toURI();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to get resource URI", e);
        }
    }

    public String getResource(String relativePath) {
        return getResource(relativePath, new HashMap<>());
    }

    public String getResource(String relativePath, Map<String, String> placeholderMap) {
        String result;
        try {
            result = Files.readString(rootPath.resolve(relativePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load resource file. Path: " + relativePath, e);
        }
        for (Entry<String, String> entry : placeholderMap.entrySet()) {
            if (result.contains(entry.getKey())) {
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public InputStream getResourceStream(String relativePath) {
        try {
            File resourceFile = rootPath.resolve(relativePath).toFile();
            return new BufferedInputStream(new FileInputStream(resourceFile));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Failed to open resource file as stream", e);
        }
    }
}