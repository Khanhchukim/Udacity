package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.AuthenticationService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
public class LoginController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final Map<String, String> commonModelObject;
    private static final String USER_INFO_ERROR = "userInfoError";
    private static final String IS_LOGGED_OUT = "isLoggedOut";
    private static final String IS_SIGNUP_SUCCESS = "isSignUpSuccess";
    public LoginController(UserService userService,
                           AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.commonModelObject = new HashMap<>();
        this.commonModelObject.put(USER_INFO_ERROR, "false");
        this.commonModelObject.put(IS_LOGGED_OUT, "false");
        commonModelObject.put("file", "active show");
        commonModelObject.put("fileTabPanel", "show active");
    }
    
    private void resetCommonModelObject() {
        commonModelObject.put(USER_INFO_ERROR, "false");
        commonModelObject.put(IS_LOGGED_OUT, "false");
        commonModelObject.put(IS_SIGNUP_SUCCESS, "false");
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginView(Model model, HttpSession session) {
        resetCommonModelObject();
        if (!Objects.isNull(session.getAttribute("isLogout")) &&
                (boolean)session.getAttribute("isLogout")) {
            commonModelObject.put(IS_LOGGED_OUT, String.valueOf(session.getAttribute("isLogout")));
        }
        commonModelObject.put(IS_SIGNUP_SUCCESS, String.valueOf(session.getAttribute(IS_SIGNUP_SUCCESS)));
        model.addAllAttributes(commonModelObject);
        session.removeAttribute(IS_SIGNUP_SUCCESS);
        session.removeAttribute("isLogout");
        return "login";
    }

    @RequestMapping(value = "/login-action", method = RequestMethod.POST)
    public String loginAction(@ModelAttribute User user, Model model, HttpSession session) {
        User userSavedInDataBase = userService.getUser(user.getUsername());
        if (userService.isUsernameAvailable(user.getUsername())) {
            commonModelObject.put(USER_INFO_ERROR, "true");
        }
        if (!Objects.isNull(userSavedInDataBase)) {
            session.setAttribute("userId", userSavedInDataBase.getUserId());
        }
        model.addAllAttributes(commonModelObject);
        if(Objects.equals(commonModelObject.get(USER_INFO_ERROR), "true")) {
            return "login";
        }
        return "redirect:/files";
    }
}
