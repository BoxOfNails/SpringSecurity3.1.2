package ru.kata.spring.boot_security.demo.configs;

import jakarta.persistence.Transient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {
    private Logger logger = Logger.getLogger(getClass().getName());
    private UserService userService;
    @Transient
    @Value("${formRoles}")
    private List<String> formRoles;

    public SuccessUserHandler(UserService userService) {
        this.userService = userService;
    }
    // Spring Security использует объект Authentication, пользователя авторизованной сессии.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {

        String username = authentication.getName();

        User theUser = userService.findByUserName(username);
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute("user", theUser);
        session.setAttribute("formRoles", formRoles);

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        if (roles.contains("ROLE_ADMIN")) {

            //FIX REDIRECT
            httpServletResponse.sendRedirect("/admin/list");
        }  else if (roles.contains("ROLE_USER")) {
            httpServletResponse.sendRedirect("/user");
        } else {
            httpServletResponse.sendRedirect("/");
        }
    }
}