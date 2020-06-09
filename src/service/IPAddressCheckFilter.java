package service;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Database;

public class IPAddressCheckFilter implements Filter {

	@Override
	public void destroy() { }

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;
		HttpServletResponse httpResponse = (HttpServletResponse)servletResponse;
		String uri = httpRequest.getRequestURI();
		if (uri.equals("/cinema/application.wadl") || uri.equals("/cinema/application.wadl/xsd0.xsd")) {
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
		String ipAddress = httpRequest.getHeader("IP-Address");
		if (ipAddress == null || ipAddress.isEmpty()) {
			String message = "Brak adresu IP.";
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, message);
			return;
		}
		else {
			System.out.println("IP: " + ipAddress);
			if (Database.getInstance().isIpAddressBlocked(ipAddress)) {
				String message = "Twój adres IP zosta³ zablokowany.";
				httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, message);
				return;
			}
			chain.doFilter(servletRequest, servletResponse);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException { }
}
