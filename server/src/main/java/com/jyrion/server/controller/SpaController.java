package com.jyrion.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA fallback controller: forwards any non-API, non-asset request to /index.html
 * so that React client-side routes work without 404s.
 *
 * The pattern {@code [^\\.]*} ensures only path segments without a dot (no file
 * extension) are matched, preventing static assets from being intercepted.
 */
@Controller
public class SpaController {

    @RequestMapping(value = {
            "/{path:[^\\.]*}",
            "/{path1:[^\\.]*}/{path2:[^\\.]*}",
            "/{path1:[^\\.]*}/{path2:[^\\.]*}/{path3:[^\\.]*}"
    })
    public String forward(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/api/")) {
            return null;
        }
        return "forward:/index.html";
    }
}
