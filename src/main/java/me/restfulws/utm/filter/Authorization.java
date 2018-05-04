package me.restfulws.utm.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.restfulws.utm.model.User;

public class Authorization implements Filter {
	private static final Logger logger = LogManager.getLogger(Authorization.class);

	public Authorization() {}
	public void init(FilterConfig fConfig) throws ServletException {}
	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("validUser");
		if (user != null) {
			logger.info("User authenticated {}", user.getUsername());
			chain.doFilter(request, response);
		} else {
			logger.info("Usuario no ha sido autenticado {}", req.getRemoteAddr());
			resp.sendRedirect(req.getContextPath() + "/login");
		}
	}
}