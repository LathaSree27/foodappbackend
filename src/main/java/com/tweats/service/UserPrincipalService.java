package com.tweats.service;

import com.tweats.controller.response.LoginResponse;
import com.tweats.exceptions.UserNotFoundException;
import com.tweats.model.Role;
import com.tweats.model.User;
import com.tweats.model.UserPrincipal;
import com.tweats.repo.RoleRepository;
import com.tweats.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPrincipalService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public UserPrincipalService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = null;
        try {
            user = findUserByEmail(email);
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException("user not found");
        }
        return new UserPrincipal(user);

    }

    public User findUserByEmail(String email) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException();
        }
        return optionalUser.get();
    }

    public boolean isVendor(User user) {
        Role userRole = user.getRole();
        Role vendorRole = roleRepository.findByName("VENDOR");
        return userRole.equals(vendorRole);
    }

    public LoginResponse getLoginResponse(String email) {
        return null;
    }
}
