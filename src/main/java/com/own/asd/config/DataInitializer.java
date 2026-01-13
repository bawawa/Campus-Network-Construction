package com.own.asd.config;

import com.own.asd.model.user.User;
import com.own.asd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 创建测试营养师账号
        if (!userRepository.existsByEmail("nutritionist1@asd.com")) {
            User nutritionist1 = new User();
            nutritionist1.setName("营养师1号");
            nutritionist1.setEmail("nutritionist1@asd.com");
            nutritionist1.setPassword(passwordEncoder.encode("123456"));
            nutritionist1.setPhone("13800138001");
            nutritionist1.setRole(User.UserRole.NUTRITIONIST);
            nutritionist1.setIsActive(true);
            userRepository.save(nutritionist1);
            System.out.println("✅ 创建营养师账号: nutritionist1@asd.com / 123456");
        }

        // 创建测试家长账号
        if (!userRepository.existsByEmail("parent1@asd.com")) {
            User parent1 = new User();
            parent1.setName("张家长");
            parent1.setEmail("parent1@asd.com");
            parent1.setPassword(passwordEncoder.encode("123456"));
            parent1.setPhone("13800138002");
            parent1.setRole(User.UserRole.PARENT);
            parent1.setRelationshipType(User.RelationshipType.MOTHER);
            parent1.setIsActive(true);
            userRepository.save(parent1);
            System.out.println("✅ 创建家长账号: parent1@asd.com / 123456");
        }

        // 创建测试管理员账号
        if (!userRepository.existsByEmail("admin@asd.com")) {
            User admin = new User();
            admin.setName("系统管理员");
            admin.setEmail("admin@asd.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setPhone("13800138000");
            admin.setRole(User.UserRole.ADMIN);
            admin.setIsActive(true);
            userRepository.save(admin);
            System.out.println("✅ 创建管理员账号: admin@asd.com / 123456");
        }
    }
}

