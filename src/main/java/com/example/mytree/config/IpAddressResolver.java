package com.example.mytree.config;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.stereotype.Component;

import com.example.mytree.exception.InvalidIpAddressException;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class IpAddressResolver {

	public String resolve(HttpServletRequest request) {
		String forwardedFor = request.getHeader("X-Forwarded-For");
		String candidate = forwardedFor == null || forwardedFor.isBlank()
			? request.getRemoteAddr()
			: forwardedFor.split(",")[0].trim();

		return toIpv4(candidate);
	}

	private String toIpv4(String candidate) {
		if (candidate == null || candidate.isBlank()) {
			throw new InvalidIpAddressException("IP address is missing.");
		}

		String normalized = candidate;
		if ("::1".equals(candidate) || "0:0:0:0:0:0:0:1".equals(candidate)) {
			normalized = "127.0.0.1";
		} else if (candidate.startsWith("::ffff:")) {
			normalized = candidate.substring(7);
		}

		try {
			InetAddress address = InetAddress.getByName(normalized);
			if (address instanceof Inet4Address inet4Address) {
				return inet4Address.getHostAddress();
			}
		} catch (UnknownHostException ex) {
			throw new InvalidIpAddressException("Invalid IP address: " + candidate, ex);
		}

		throw new InvalidIpAddressException("Only IPv4 addresses are supported: " + candidate);
	}
}
