package com.example.test_jwt.controller;

import com.example.test_jwt.entity.Users;
import com.example.test_jwt.payload.LoginRequest;
import com.example.test_jwt.payload.LoginResponse;
import com.example.test_jwt.security.JwtTokenProvider;
import com.example.test_jwt.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

import static com.example.test_jwt.security.SecurityConstants.TOKEN_PREFIX;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin("*")

public class UserController {
    @Autowired
    private UsersService userService;
    @Autowired
    private DaoAuthenticationProvider authenticationManager;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/{email}")
    public ResponseEntity<Users> findUsersByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findUsersByEmail(email));
    }

    @PostMapping("/register")
    public ResponseEntity<Users> registerUsers(@RequestBody Users users) {
        return ResponseEntity.ok(userService.registerUsers(users));
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginBasic(@RequestBody Users userLogin) {
        try {
            Users loggedInUser = userService.loginBasic(userLogin);
            return ResponseEntity.ok("Đăng nhập thành công cho người dùng: " + loggedInUser.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/login-jwt")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // Xác thực thông tin đăng nhập
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassWord()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

//        // Lấy thông tin người dùng từ cơ sở dữ liệu bằng email
//        Users user = userService.findUsersByName(loginRequest.getUserName());
//        // Kiểm tra tài khoản đã được kích hoạt hay chưa
//        if (!user.isActive()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse(false, "Tài khoản chưa được kích hoạt."));
//        }

        // Tạo mã JWT nếu tài khoản đã được kích hoạt
        String jwt = TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);
        // Trả về thông tin đăng nhập thành công và mã JWT
        return ResponseEntity.ok(new LoginResponse(true, jwt));
    }

    @GetMapping("/activation")
    public ResponseEntity<String> activateUsers(@RequestParam String code) {
        Users user = userService.activateUser(code);
        if (user != null && user.isActive()) {
            String success = "Kích hoạt tài khoản " + user.getUsername() + " thành công.";
            return ResponseEntity.status(HttpStatus.OK).body(success);
        } else {
            String error = "Mã xác nhận không hợp lệ hoặc tài khoản đã được kích hoạt trước đó.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/{username}/enabled/{status}")
    public ResponseEntity<String> updateUserEnabledStatus(@PathVariable("username") String username, @PathVariable("status") boolean enabled) {
        try {
            userService.unlockUser(username, enabled);
            String status = enabled ? "khóa" : "mở Khóa";
            String message = "Tài khoản " + username + " đã " + status;
            return ResponseEntity.ok(message);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
