package net.bhpachulski.tddcriteria.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author bhpachulski
 */
public final class PropertiesUtils {

    public Properties loadProperties(String fileName) {
        Properties configData = null;
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            
            if (in != null) {
                configData = new Properties();
                configData.load(in);
            }

            return configData;
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível localizar a propriedade " + fileName + ". " + e.getMessage());
        }
    }

}
