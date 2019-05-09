/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.security;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SecurityContextUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityContextUtility.class);

    private static final String ANONYMOUS = "anonymous";

    private SecurityContextUtility() {
    }

    public static String getUserName() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String username = ANONYMOUS;

        if (null != authentication) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                username = springSecurityUser.getUsername();

            } else if (authentication.getPrincipal() instanceof String) {
                username = (String) authentication.getPrincipal();

            } else {
                LOGGER.debug("User details not found in Security Context");
            }
        } else {
            LOGGER.debug("Request not authenticated, hence no user name available");
        }

        return username;
    }

    public static Set<String> getUserRoles() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Set<String> roles = new HashSet<>();

        // TODO: Authorities are very similar in spring. The roles here will return the
        // authorities, which is ROLE_<role-name>. We should strip the _ROLE
        // to be 100% correct.
        if (null != authentication) {
            authentication.getAuthorities().forEach(e -> roles.add(e.getAuthority()));
        }
        return roles;
    }

    public static Set<String> getUserAuthorities() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        Set<String> roles = new HashSet<>();

        if (null != authentication) {
            authentication.getAuthorities().forEach(e -> roles.add(e.getAuthority()));
        }
        return roles;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getClaimsFromJwt() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> map = objectMapper.convertValue(authentication.getDetails(), Map.class);

        // create a token object to represent the token that is in use.
        Jwt jwt = JwtHelper.decode((String) map.get("tokenValue"));

        try {
            Map<String, Object> claims = objectMapper.readValue(jwt.getClaims(), Map.class);
            LOGGER.debug("Claims VAL {}", claims);

            return claims;
        } catch (JsonParseException e) {
            LOGGER.error(e.getMessage());
        } catch (JsonMappingException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }

    public static String getUserIdFromJwt() {
        Map<String, Object> claims = getClaimsFromJwt();

        if (claims != null) {
            return (String) claims.get("sub");
        }

        return null;
    }
}
