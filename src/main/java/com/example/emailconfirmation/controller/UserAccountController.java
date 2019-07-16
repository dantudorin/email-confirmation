package com.example.emailconfirmation.controller;

import com.example.emailconfirmation.models.ConfirmationToken;
import com.example.emailconfirmation.models.User;
import com.example.emailconfirmation.repository.ConfirmatonTokenRepository;
import com.example.emailconfirmation.repository.UserRepository;
import com.example.emailconfirmation.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@RestController
public class UserAccountController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmatonTokenRepository confirmatonTokenRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public ModelAndView displayResgistration(ModelAndView modelAndView, User user){
        modelAndView.addObject("user",user);
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public ModelAndView registerUser(ModelAndView modelAndView,@RequestBody User user){

        System.out.println(user.toString());
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if(existingUser.isPresent()){
            modelAndView.addObject("message","This email already exists!");
            modelAndView.setViewName("error");
        }else{
            userRepository.save(user);
            ConfirmationToken confirmationToken = new ConfirmationToken(user);

            confirmatonTokenRepository.save(confirmationToken);

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(user.getEmail());
            simpleMailMessage.setSubject("Apuca-te de treaba si nu mai dormi");
            simpleMailMessage.setFrom("noreply.equatorial@gmail.com");
            simpleMailMessage.setText("\"To confirm your account, please click here : \"\n" +
                    "            +\"http://localhost:8080/confirm-account?token=" + confirmationToken.getConfirmationToken());

            emailSenderService.sendEmail(simpleMailMessage);

            modelAndView.addObject("emailId",user.getEmail());
            modelAndView.setViewName("successfulRegistration");
        }

        return modelAndView;
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token") String confirmationToken){
        Optional<ConfirmationToken> token = confirmatonTokenRepository.findByConfirmationToken(confirmationToken);

        if(token.isPresent()){
            Optional<User> user = userRepository.findByEmail(token.get().getUser().getEmail());
            user.get().setEnabled(true);

            userRepository.save(user.get());

            modelAndView.setViewName("accountVerified");
        }else{
            modelAndView.addObject("message","the link is invalid or borken");
            modelAndView.setViewName("error");
        }

        return modelAndView;

    }
}
