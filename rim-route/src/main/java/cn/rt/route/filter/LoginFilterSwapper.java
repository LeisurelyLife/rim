package cn.rt.route.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author ruanting
 * @date 2019/11/27
 */
public class LoginFilterSwapper extends HttpServletRequestWrapper {

    private static final Logger log = LoggerFactory.getLogger(LoginFilterSwapper.class);

    private final String body;

    public LoginFilterSwapper(HttpServletRequest request) {
        super(request);
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = request.getReader();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str);
            }
        } catch (Exception e) {
            log.error("LoginFilterSwapper转化数据异常", e);
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        body = builder.toString();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }
            @Override
            public boolean isReady() {
                return false;
            }
            @Override
            public void setReadListener(ReadListener readListener) {
            }
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    public String getBody() {
        return this.body;
    }

}
