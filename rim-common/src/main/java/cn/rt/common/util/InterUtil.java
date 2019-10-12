package cn.rt.common.util;

import cn.rt.common.common.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * @author ruanting
 * @date 2019/10/12
 * 国际化工具
 */
public class InterUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterUtil.class);

    private static MessageSource messageSource;

    /**
     * 获取国际化标签
     * @param request
     * @param interMark
     * @return
     */
    public static String interInfo(HttpServletRequest request, String interMark) {
        try {
            Locale locale = request.getLocale();
            if (messageSource == null) {
                messageSource = SpringContextHolder.getBean(MessageSource.class);
            }
            return messageSource.getMessage(interMark, null, locale);
        } catch (Exception e) {
            LOGGER.error("获取国际化标签失败", e);
            return "Unknown result";
        }
    }

    /**
     * 获取国际化标签
     * @param request
     * @param interMark
     * @param param
     * @return
     */
    public static String interInfo(HttpServletRequest request, String interMark, Object[] param) {
        try {
            Locale locale = request.getLocale();
            if (messageSource == null) {
                messageSource = SpringContextHolder.getBean(MessageSource.class);
            }
            return messageSource.getMessage(interMark, param, locale);
        } catch (Exception e) {
            LOGGER.error("获取国际化标签失败", e);
            return "Unknown result";
        }
    }

}
