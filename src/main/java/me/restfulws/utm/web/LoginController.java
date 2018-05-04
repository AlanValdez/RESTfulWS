package me.restfulws.utm.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import me.restfulws.utm.form.LoginForm;
import me.restfulws.utm.model.User;
import me.restfulws.utm.service.UserService;

@Controller
public class LoginController {
    @Autowired
    UserService userService;

	private static final Logger log = LogManager.getLogger();

    @RequestMapping(value = "login", 
    				method = RequestMethod.GET)
    public ModelAndView login(Map<String, Object> model, HttpSession session) {
        if(session.getAttribute("username") != null) {
            return this.getHomeRedirect();
        } 
        List<String> warnings = new ArrayList<String>();
        warnings.add("Restricted resource. Login first.");
        model.put("loginWarnings",warnings );
        model.put("loginForm", new LoginForm());
        return new ModelAndView("login");
    }

    @RequestMapping(value = "login", 
    				method = RequestMethod.POST)
    public ModelAndView login(Map<String, Object> model, HttpSession session,
                              HttpServletRequest request, LoginForm form) {
    	List<String> warnings = new ArrayList<String>();
        if(session.getAttribute("validUser") != null) {
        	log.info("Already logged in {}", session.getAttribute("username"));
            return this.getHomeRedirect();
        }
        if(form.getUsername() == null || form.getPassword() == null) {
        	log.warn("Login failed for user {}", form.getUsername());
        	warnings.add("Username or passowrd empty.");
            form.setPassword(null);
            model.put("loginWarnings", warnings);
            model.put("loginForm", form);
            return new ModelAndView("login");
        } 
        else if(!userService.login(form.getUsername(), form.getPassword())) {
            log.warn("Login failed for user {}", form.getUsername());
            warnings.add("Invalid Username or password.");
            form.setPassword(null);
            model.put("loginWarnings", warnings);
            model.put("loginForm", form);
            return new ModelAndView("login");
        } 

        log.debug("User {} successfully logged in.", form.getUsername());
        session.setAttribute("validUser", userService.getUser(form.getUsername()));
        request.changeSessionId();
        return this.getHomeRedirect();
    }

    private ModelAndView getHomeRedirect() {
        return new ModelAndView(new RedirectView("/dashboard/home", true, false));
    }

    @RequestMapping("logout")
    public View logout(HttpSession session, Map<String, Object> model) {
    	User user = (User) session.getAttribute("validUser");
        log.debug("User {} logged out.", user.getUsername());
        session.invalidate();
        model.put("loginWarnings", new ArrayList<String>().add("Successfully logged out."));
        return new RedirectView("/login", true, false);
    }    
}