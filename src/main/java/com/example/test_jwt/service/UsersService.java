package com.example.test_jwt.service;

import com.example.test_jwt.entity.Roles;
import com.example.test_jwt.entity.Users;
import com.example.test_jwt.repository.IRolesRepo;
import com.example.test_jwt.repository.IUsersRepo;
import com.example.test_jwt.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class UsersService {
    @Autowired
    private IUsersRepo iUsersRepo;
    @Autowired
    private IRolesRepo iRolesRepo;
    @Autowired
    private EmailService emailService;

    public Users findUsersByEmail(String email) {
        return iUsersRepo.findUsersByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không thể tìm thấy user có email: " + email));
    }

    public Users registerUsers(Users newUser) {
        Optional<Users> users = iUsersRepo.findUsersByEmail(newUser.getEmail());
        if (users.isPresent()) {
            throw new RuntimeException("Email đã tồn tại: " + newUser.getEmail());
        }

        Roles roles = iRolesRepo.findRolesById(2L);
        newUser.setActive(false);
        newUser.setRoles(roles);
        newUser.setLocked(false);

        String confirmationCode = randomCodeEmail(newUser.getEmail());
        newUser.setCodeActivated(confirmationCode);

        return iUsersRepo.save(newUser);
    }

    public Users loginBasic(Users userLogin) {
        String userName = userLogin.getUsername();
        String password = userLogin.getPassword();

        Optional<Users> existingUser = iUsersRepo.findUsersUserName(userName);
        if (existingUser.isPresent()) {
            Users user = existingUser.get();
            if (!user.isActive()) {
                throw new RuntimeException("Tài khoản chưa được kích hoạt.");
            }

            if (!user.getPassword().equals(password)) {
                throw new RuntimeException("Mật khẩu không chính xác.");
            }

            if (user.isLocked()) {
                throw new RuntimeException("Tài khoản bị khóa.");
            }

            return user;

        } else {
            throw new RuntimeException("Người dùng không tồn tại.");
        }
    }

    public Users activateUser(String code) {
        Users user = iUsersRepo.findUsersByActive(code);
        if (user != null && !user.isActive()) {
            user.setActive(true);
            iUsersRepo.save(user);
        }
        return user;
    }

    public void unlockUser(String userName, boolean locked) {
        iUsersRepo.updateEnabledLocked(userName, locked);
    }

    public String randomCodeEmail(String email) {
        Random rand = new Random();
        int randomNum = rand.nextInt(900000) + 100000;
        String to = email;
        String subject = "OTP Kích Hoạt";
        String text = String.valueOf(randomNum);
        String content = "Xin Chào ...!\n" +
                "Bạn hoặc ai đó đã dùng email này để đăng ký tài khoản tại web Mrdunghr\n" +
                "\n" +
                "MÃ XÁC NHẬN CỦA BẠN LÀ  : " + text + "\n" +
                "Nhấn vào Link này để kích hoạt nhanh: " +
                "http://localhost:8080/api/v1/users/activation?code=" + text +
                "\n" +
                "--------------------------------------\n" +
                " + Phone  : (+84)382.564.626\n" +
                " + Email  : mrdunghr@gmail.com\n" +
                " + Address: Mông Dương - TP Cẩm Phả - Quảng Ninh\n";
        emailService.sendMail(to, subject, content);
        return text;

    }
}
