package com.company.movierating.controller.filter;

import java.io.IOException;

import com.company.movierating.controller.util.JspConstants;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthorizationFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String command = req.getParameter("command");
        if (requiresAuthorization(command)) {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("user") == null) {
                req.setAttribute("errorMessage", "Authorization needed");
                req.getRequestDispatcher(JspConstants.SIGN_IN_FORM).forward(req, res);
                return;
            }
        }
        chain.doFilter(req, res);
    }

    private boolean requiresAuthorization(String command) {
        return switch (command) {
            case "users", "user", "sign_in_form", "sign_in", "create_user_form", "create_user", "error" -> false;
            default -> true;
        };
    }

}
