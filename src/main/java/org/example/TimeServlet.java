package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@WebServlet(value = "/time")

public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;
    private static final String TIMEZONE_KEY = "timezone";
    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("src/main/webapp/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String timeZone = "UTC";
        if (req.getParameter(TIMEZONE_KEY) != null){
            timeZone = req.getParameter(TIMEZONE_KEY);
            resp.addCookie(new Cookie(TIMEZONE_KEY, timeZone));
        }else {
            Cookie[] cookies = req.getCookies();
            if(cookies != null){
                for (Cookie cookie : cookies) {
                    if(cookie.getName().equals(TIMEZONE_KEY)){
                        timeZone = cookie.getValue();
                        break;
                    }
                }
            }
        }


        ZoneId zoneId = ZoneId.of(timeZone);
        ZoneOffset zoneOffset = zoneId.getRules().getOffset(Instant.now());

        LocalDateTime dateTime = LocalDateTime.now(zoneOffset);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(String.format("yyyy-MM-dd HH:mm:ss '%s'", timeZone));
        String formattedDateTime = dateTime.format(formatter);

        resp.setContentType("text/html; charset=utf-8");
        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("time", formattedDateTime)
        );
        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }


}
