package com.chrispbacon.chesschat.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class PageController {

  private static final Logger log = LoggerFactory.getLogger(PageController.class);

  @GetMapping("/home")
  public ModelAndView welcome() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("index.html");
    return modelAndView;
  }

  @GetMapping("/login")
  public ModelAndView login() {
    log.info("test");
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("login.html");
    log.info(modelAndView.getViewName());
    return modelAndView;
  }

  @GetMapping("/register")
  public ModelAndView register() {
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("register.html");
    return modelAndView;
  }
}
