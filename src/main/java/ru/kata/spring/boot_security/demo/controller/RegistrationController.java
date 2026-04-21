package ru.kata.spring.boot_security.demo.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.dto.WebUser;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.logging.Logger;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private Logger logger = Logger.getLogger(getClass().getName());
    private UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showMyRegistrationForm")
    public String showMyRegistrationForm(Model theModel) {
        theModel.addAttribute("webUser", new WebUser());
        return "register/registration-form";
    }

    @PostMapping("/processRegistrationForm")
    public String processRegistrationForm(
            @Valid @ModelAttribute("webUser") WebUser webUser,
            BindingResult bindingResult, Model theModel) {
        String username = webUser.getUsername();
        logger.info("Processing registration form for: " + username);

        //form validation
        if (bindingResult.hasErrors()) {
            return "register/registration-form";
        }

        User existing = userService.findByUserName(username);

        if(existing != null) {
            theModel.addAttribute("webUser", new WebUser());
            theModel.addAttribute("registrationError", "User name already exists.");

            logger.warning("User name already exists.");

            return "register/registration-form";
        }

        userService.register(webUser);

        logger.info("Successfully created user: " + username);


        return "register/registration-confirmation";

    }
}
