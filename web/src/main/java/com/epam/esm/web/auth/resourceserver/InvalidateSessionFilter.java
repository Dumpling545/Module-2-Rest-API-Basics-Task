package com.epam.esm.web.auth.resourceserver;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Invalidates session if request url does not match one of approved paths
 */
public class InvalidateSessionFilter extends OncePerRequestFilter {
	private final List<AntPathRequestMatcher> pathRequestMatchers;
	public InvalidateSessionFilter(List<String> matchPatterns) {
		pathRequestMatchers = matchPatterns.stream().map(AntPathRequestMatcher::new).toList();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {
		filterChain.doFilter(request, response);
		HttpSession session = request.getSession(false);
		if(pathRequestMatchers.stream().noneMatch(m -> m.matches(request)) && session != null){
			session.invalidate();
		}
	}
}
