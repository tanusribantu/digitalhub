package com.digitalhub.service;

import com.digitalhub.dto.AuthRequest;
import com.digitalhub.dto.AuthResponse;
import com.digitalhub.dto.RegisterRequest;
import com.digitalhub.exception.BadRequestException;
import com.digitalhub.exception.UnauthorizedException;
import com.digitalhub.model.Role;
import com.digitalhub.model.SellerProfile;
import com.digitalhub.model.User;
import com.digitalhub.repository.SellerProfileRepository;
import com.digitalhub.repository.UserRepository;
import com.digitalhub.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerProfileRepository sellerProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email address already in use!");
        }

        Role role = req.getRole() != null ? req.getRole() : Role.ROLE_CUSTOMER;
        User user = new User(
                req.getFullName(),
                req.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(req.getPassword()),
                role
        );
        user = userRepository.save(user);

        String storeName = null;
        if (role == Role.ROLE_SELLER) {
            storeName = req.getStoreName() != null && !req.getStoreName().isBlank() ?
                    req.getStoreName() : req.getFullName() + "'s Studio";
            SellerProfile profile = new SellerProfile(user, storeName, req.getBio());
            sellerProfileRepository.save(profile);
        }

        String token = jwtTokenProvider.generateTokenForEmail(user.getEmail(), user.getId(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole(), storeName);
    }

    public AuthResponse login(AuthRequest req) {
        User user = userRepository.findByEmail(req.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new UnauthorizedException("Account is disabled. Please contact admin.");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String storeName = null;
        if (user.getRole() == Role.ROLE_SELLER) {
            storeName = sellerProfileRepository.findByUserId(user.getId())
                    .map(SellerProfile::getStoreName)
                    .orElse(null);
        }

        String token = jwtTokenProvider.generateTokenForEmail(user.getEmail(), user.getId(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getFullName(), user.getEmail(), user.getRole(), storeName);
    }
}