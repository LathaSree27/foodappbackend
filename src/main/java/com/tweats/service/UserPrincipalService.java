package com.tweats.service;

import com.tweats.controller.response.LoginResponse;
import com.tweats.exceptions.UserNotFoundException;
import com.tweats.model.Role;
import com.tweats.model.User;
import com.tweats.model.UserPrincipal;
import com.tweats.repo.RoleRepository;
import com.tweats.repo.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserPrincipalService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = findUserByEmail(email);
            return new UserPrincipal(user);
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException("user not found");
        }
    }

    public User findUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public boolean isVendor(User user) {
        Role userRole = user.getRole();
        Role vendorRole = roleRepository.findByName("VENDOR");
        return userRole.equals(vendorRole);
    }

    public LoginResponse getLoginResponse(String email) throws UserNotFoundException {
        User user = findUserByEmail(email);
        return new LoginResponse(email, user.getRole().getName());
    }
}
