package com.example.backend.web.security;

import com.example.backend.infrastructure.security.CustomUserDetails;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterAnnotation(CurrentUser.class) != null
            && parameter.getParameterType().equals(Long.class);
  }

  @Override
  public Object resolveArgument(
          MethodParameter parameter,
          ModelAndViewContainer mavContainer,
          NativeWebRequest webRequest,
          WebDataBinderFactory binderFactory
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("User not authenticated");
    }

    Object principal = authentication.getPrincipal();

    // If principal is CustomUserDetails, extract the userId
    if (principal instanceof CustomUserDetails) {
      return ((CustomUserDetails) principal).getUserId();
    }

    // Fallback: if principal is a Long (shouldn't happen in normal cases)
    if (principal instanceof Long) {
      return principal;
    }

    throw new IllegalStateException("Cannot extract userId from principal: " + principal.getClass().getSimpleName());
  }
}