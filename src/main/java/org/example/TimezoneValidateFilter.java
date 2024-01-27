package org.example;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {


    @Override
    public void doFilter(HttpServletRequest req,
                            HttpServletResponse resp,
                            FilterChain chain) throws IOException, ServletException {

        String timeZone = req.getParameter("timezone");

        if (timeZone != null){
            try{
                ZoneId.of(timeZone);
                chain.doFilter(req, resp);
            }catch (DateTimeException e){
                resp.setStatus(400);
                resp.setContentType("text/html; charset=utf-8");
                resp.getWriter().write("Invalid timezone");
                resp.getWriter().close();
            }
        }else {
            chain.doFilter(req, resp);
        }

    }
}
