package com.udacity.jwdnd.course1.cloudstorage.controllers;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.EncryptionService;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SignupController {
    private final UserService userService;
    private final EncryptionService encryptionService;
    private final Map<String, String> commonModelObject;
    private static final String IS_SIGNUP_SUCCESS = "isSignUpSuccess";
    private static final String IS_REGISTERED = "isRegistered";
    @Value("${ENCRYPTION_KEY}")
    private String encryptionKey;

    public SignupController(
            UserService userService,
            EncryptionService encryptionService
    ) {
        this.userService = userService;
        this.encryptionService = encryptionService;
        commonModelObject = new HashMap<>(){};
    }

    private void resetCommonModelObject() {
        commonModelObject.put(IS_REGISTERED, "false");
        commonModelObject.put(IS_SIGNUP_SUCCESS, "false");
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    private String getSignUpPage(Model model) {
        resetCommonModelObject();
        User mockUser = new User(null, "test", "test", "test", "test", "test");
        model.addAttribute("user", mockUser);
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    private String postSignUp (@ModelAttribute User user, Model model, HttpServletResponse httpServletResponse, HttpSession session) throws IOException {
        String userName = user.getUsername();
        resetCommonModelObject();
        if (!userService.isUsernameAvailable(userName)) {
            commonModelObject.put(IS_REGISTERED, "true");
            model.addAllAttributes(commonModelObject);
            return "signup";
        }
        user.setPassword(encryptionService.encryptValue(user.getPassword(), encryptionKey));
        int index =  userService.createUser(user);

        if (index > -1) {
            session.setAttribute(IS_SIGNUP_SUCCESS, "true");
            model.addAllAttributes(commonModelObject);
            httpServletResponse.sendRedirect("/login");
        } else {
            session.setAttribute(IS_SIGNUP_SUCCESS, "false");
            model.addAllAttributes(commonModelObject);
        }
        return "signup";
    }
}
