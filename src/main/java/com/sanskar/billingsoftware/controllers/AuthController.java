package com.sanskar.billingsoftware.controllers;

import com.sanskar.billingsoftware.io.AuthRequest;
import com.sanskar.billingsoftware.io.AuthResponse;
import com.sanskar.billingsoftware.service.UserService;
import com.sanskar.billingsoftware.service.impl.AppUserDetailsService;
import com.sanskar.billingsoftware.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AppUserDetailsService appUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) throws Exception {
        // authenticating the user with Authentication Manager
        authenticate(request.getEmail(), request.getPassword());

        // Getting the user details with the help of AppUserDetailsService
        final UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());

        // generate the Token with the help of jwtUtils by passing userDetails
        final String jwtToken = jwtUtil.generateToken(userDetails);

        // fetching the user role from the repo
        String role = userService.getUserRole(request.getEmail());

        return new AuthResponse(request.getEmail(), jwtToken, role);
    }

    public void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new Exception("User disabled");
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or password is incorrect");
        }
    }

    @PostMapping("/encode")
    public String encodePassword(@RequestBody Map<String, String> request) {
        return passwordEncoder.encode(request.get("password"));
    }
}
