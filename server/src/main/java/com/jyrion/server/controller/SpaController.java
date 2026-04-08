package com.jyrion.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA fallback controller: forwards any non-API, non-asset request to /index.html
 * so that React Router client-side routes work without 404s.
 */
@Controller
public class SpaController {

    @RequestMapping(value = {
            "/{path:[^\\.]*}",
            "/{path:[^\\.]*}/**"
    })
    public String forward(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Do not forward API calls or paths with a file extension (static assets)
        if (path.startsWith("/api/")) {
            return null;
        }
        return "forward:/index.html";
    }
}
