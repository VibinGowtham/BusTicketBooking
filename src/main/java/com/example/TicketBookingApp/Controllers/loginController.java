package com.example.TicketBookingApp.Controllers;
import java.util.List;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.security.Principal;
import java.util.ArrayList;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.TicketBookingApp.Model.MyUser;
import com.example.TicketBookingApp.Services.MyUserDetailsService;
import com.example.TicketBookingApp.UserRepository.UserRepository;
import com.example.TicketBookingApp.Util.Util;


@Controller
public class loginController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	 private Util util;
	
	@Autowired
	private MyUserDetailsService myUserDetailsService;
	
	
	@PostMapping("/authenticate")
	@ResponseBody
	public ResponseEntity<?> createJwtToken(String userName,String password) throws Exception
	{
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName,password));
		}
		catch(AuthenticationException e) {
			throw new Exception("Incorrect Username and Passowrd",e);}

			UserDetails userdetails= myUserDetailsService.loadUserByUsername(userName);
			String accessToken=util.generateToken(userdetails);
			String refreshToken=util.generateRefreshToken(userdetails);
			System.out.print(accessToken);
			System.out.print(refreshToken);
			return ResponseEntity.ok("Access-Token :"+accessToken +" \n"+"Refresh-Token :"+refreshToken);
	}
	
	@GetMapping(path="/home")
	//@ResponseBody
	public ModelAndView displayHomepage(Principal principal) {
		String name="vibin";
		ModelAndView mv=new ModelAndView("Homepage.jsp");
		mv.addObject("name",name);
		return mv;
	}
	
	@GetMapping(path="/register")
	public String displayRegisterpage() {
		return "Register.jsp";
	}
	
	 @GetMapping(path="/login") 
	    public String login() {
	    	return "Login.jsp";
	    }
	 
    @PostMapping(path="/register") 
    public String registerUser(MyUser user) {
    	if(userRepository.findAll().isEmpty() || userRepository.findByEmail(user.getEmail()).isEmpty()) 
 		    {
    		userRepository.save(user);
        		return "Login.jsp";
		      }
    		else return "AlreadyRegistered.jsp";
    	}
    
    @PostMapping(path="/login") 
    public ResponseEntity<?> loginUser(MyUser user) {
      	if(userRepository.findAll().isEmpty()) 
 		    {
    		return (ResponseEntity<?>) ResponseEntity.notFound();
		      }
    	else {
    		if(userRepository.findByEmail(user.getEmail())!=null) {
    			return (ResponseEntity<?>) ResponseEntity.noContent();
    		}
    		else{
    			List<MyUser> tempUser=userRepository.findByEmail(user.getEmail());
   			for(MyUser object:tempUser) {
    				if(object.getEmail().equals(user.getEmail()) && object.getPassword().equals(user.getPassword())) return ResponseEntity.ok(user);
    			}
    		}
    	}
		return (ResponseEntity<?>) ResponseEntity.notFound();
   }
    
    @GetMapping(path="/all") 
    public @ResponseBody Iterable<MyUser> gettAll() {
        return userRepository.findAll();
    }
}
