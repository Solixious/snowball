package dev.indian.snowball.controller;

import dev.indian.snowball.constants.UrlPath;
import dev.indian.snowball.model.auth.KiteAuthentication;
import dev.indian.snowball.service.KiteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(UrlPath.LOGIN)
@RequiredArgsConstructor
public class LoginController {

    private final KiteService kiteService;

    @GetMapping
    public String redirectToKiteLogin() {
        return "redirect:" + kiteService.getLoginUrl();
    }

    @GetMapping("/callback")
    public String handleCallback(
            @RequestParam(value = "request_token", required = false) String requestToken,
            @RequestParam(value = "status", required = false) String status,
            HttpServletRequest request) {

        if (status != null && status.equals("success") && requestToken != null) {
            try {
                KiteAuthentication authentication = kiteService.generateSession(requestToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Create a session if one doesn't exist
                HttpSession session = request.getSession(true);
                session.setAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        SecurityContextHolder.getContext()
                );

                return "redirect:/";
            } catch (Exception e) {
                return "redirect:/?error=auth_failed";
            }
        }

        // If status is not success or parameters are missing
        return "redirect:/?error=invalid_callback";
    }
}
