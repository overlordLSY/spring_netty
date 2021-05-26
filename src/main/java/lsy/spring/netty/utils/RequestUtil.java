package lsy.spring.netty.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RequestUtil {

    /**
     * 获取url的请求参数MAP
     */
    public static Map<String, String> getParameterFromUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return Collections.emptyMap();
        }

        Map<String, String> parameter = new ConcurrentHashMap<>();
        int index = url.indexOf("?");
        if (index >= 0) {
            url = url.substring(url.indexOf("?") + 1);
        }
        if (StringUtils.isNotBlank(url)) {
            for (String item : url.split("&")) {
                if (StringUtils.isBlank(item)) {
                    continue;
                }
                index = item.indexOf("=");
                if (index > 0 && index < item.length() - 1) {
                    parameter.put(item.substring(0, index), item.substring(index + 1));
                }
            }
        }
        return parameter;
    }

}
