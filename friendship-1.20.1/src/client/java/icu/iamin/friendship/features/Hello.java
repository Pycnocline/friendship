package icu.iamin.friendship.features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hello {
    private static final Logger LOGGER = LoggerFactory.getLogger(Hello.class);

    public String helloWorld(){
        LOGGER.info("Feature activated: Hello-helloWorld");

        return "你好！";
    }
}
