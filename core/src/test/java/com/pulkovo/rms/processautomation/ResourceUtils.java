package com.pulkovo.rms.processautomation;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.assertj.core.util.Files;
import org.springframework.core.io.Resource;

@UtilityClass
public class ResourceUtils {

    public static String contentOf(Resource resource) {
        try {
            return Files.contentOf(resource.getFile(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}