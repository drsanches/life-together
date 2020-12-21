package ru.drsanches.auth_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import ru.drsanches.common.enumeration.Role;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@Component
public class AdminFilter extends GenericFilterBean {

    @Autowired
    private TokenStore tokenStore;

    private final Logger log = LoggerFactory.getLogger(AdminFilter.class);

    //TODO: Move regex to config
    private final Pattern adminUriPattern = Pattern.compile("/actuator.*");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse= (HttpServletResponse) response;
        String token = getToken(httpRequest.getHeader("Authorization"));
        String uri = ((HttpServletRequest) request).getRequestURI();
        if (adminUriPattern.matcher(uri).matches()) {
            if (isAdmin(token)) {
                chain.doFilter(request, response);
            } else {
                httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
                httpResponse.getOutputStream().flush();
                log.warn("Trying to access admin endpoint '{}' from '{}'", uri, request.getRemoteAddr());
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean isAdmin(String token) {
        if (token == null) {
            return false;
        }
        return tokenStore.readAuthentication(token).getUserAuthentication().getAuthorities().stream()
                .anyMatch(x -> x.getAuthority().equals(Role.ADMIN.name()));
    }

    private String getToken(String authorization) {
        if (authorization == null) {
            return null;
        }
        try {
            return authorization.substring(authorization.indexOf(" ") + 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }
}