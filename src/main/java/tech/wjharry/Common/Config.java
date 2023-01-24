package tech.wjharry.Common;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class Config {
    public Object readConf(String confName){
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("config.yml");
        Map<String, Object> configMap = (Map<String, Object>) yaml.load(inputStream);
        return configMap.get(confName);
    }
}
