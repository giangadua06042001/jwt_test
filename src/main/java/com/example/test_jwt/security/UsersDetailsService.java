package com.example.test_jwt.security;

import com.example.test_jwt.entity.Users;
import com.example.test_jwt.repository.IUsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsersDetailsService implements UserDetailsService {
    @Autowired
    private IUsersRepo iUsersRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = iUsersRepo.findUsersByUserName(username);
        return users;
    }
}