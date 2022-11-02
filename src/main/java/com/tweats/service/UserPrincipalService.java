package com.tweats.service;

import com.tweats.model.User;
import com.tweats.model.UserPrincipal;
import com.tweats.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserPrincipalService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    public UserPrincipalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findUserByEmail(email);
        return new UserPrincipal(user);

    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("user not found"));
    }

    public String getRoleName(String email) {
        User user = findUserByEmail(email);
        return user.getRole().getName();

    }
}
