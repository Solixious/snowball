package dev.indian.snowball.controller;

import dev.indian.snowball.constants.AppConfigKey;
import dev.indian.snowball.constants.UrlPath;
import dev.indian.snowball.model.auth.KiteAuthentication;
import dev.indian.snowball.service.KiteService;
import dev.indian.snowball.service.AppConfigService;
import dev.indian.snowball.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
    private final AppConfigService appConfigService;

    @GetMapping
    public String redirectToKiteLogin(HttpServletRequest request) {
        String loginTime = appConfigService.getConfigValue(AppConfigKey.LOGIN_TIME).orElse(null);
        if (TokenUtil.isLastLoginToday(loginTime)) {
            KiteAuthentication authentication = kiteService.resumeKiteSession();
            if (authentication != null) {
                return createSessionAndRedirect(request, authentication);
            }
        }
        return "redirect:" + kiteService.getLoginUrl();
    }

    @GetMapping("/callback")
    public String handleCallback(
            @RequestParam(value = "request_token", required = false) String requestToken,
            @RequestParam(value = "status", required = false) String status,
            HttpServletRequest request) {

        if (status != null && status.equals("success") && requestToken != null) {
            try {
                KiteAuthentication authentication = kiteService.generateKiteSession(requestToken);
                return createSessionAndRedirect(request, authentication);
            } catch (Exception e) {
                return "redirect:/?error=auth_failed";
            }
        }

        // If status is not success or parameters are missing
        return "redirect:/?error=invalid_callback";
    }

    @NotNull
    private static String createSessionAndRedirect(HttpServletRequest request, KiteAuthentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
        return "redirect:/";
    }
}
