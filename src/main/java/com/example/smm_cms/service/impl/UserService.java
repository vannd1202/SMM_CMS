package com.example.smm_cms.service.impl;

import com.example.smm_cms.base.BaseException;
import com.example.smm_cms.common.Role;
import com.example.smm_cms.base.BaseService;
import com.example.smm_cms.base.ResponseData;
import com.example.smm_cms.base.ResponsePage;
import com.example.smm_cms.dto.request.user.CustomerRequest;
import com.example.smm_cms.dto.request.user.LoginRequest;
import com.example.smm_cms.dto.request.user.RegisterRequest;
import com.example.smm_cms.dto.response.customer.CustomerResponse;
import com.example.smm_cms.dto.response.login.LoginResponse;
import com.example.smm_cms.entity.User;
import com.example.smm_cms.repository.UserRepository;
import com.example.smm_cms.security.JwtTokenProvider;
import com.example.smm_cms.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService extends BaseService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtService;

    @Transactional
    public ResponseData<String> register(RegisterRequest request) {
        ResponseData<String> responseData = new ResponseData<>();
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new BaseException(300,"Username already exists");
            }

            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.ROLE_USER)
                    .build();

            userRepository.save(user);
            responseData.success(user.getUsername());

        }catch (BaseException e) {
            LOGGER.error("Lỗi đăng ký: {}", e.getMessage());
            responseData.setCode(e.getCode());
            responseData.setMessage(e.getMessage());
            return responseData;
        }

        return responseData;
    }

    public ResponseData<?> login(LoginRequest request) {
        LoginResponse response;
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BaseException(10001,"Invalid username or password"));

            if (!passwordEncoder.matches(
                    request.getPassword(),
                    user.getPassword())) {

                throw new RuntimeException("Invalid username or password");
            }

            String token = jwtService.generateToken(user.getUsername());

             response= LoginResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build();
        }catch (BaseException e) {
            LOGGER.error("Lỗi đăng nhập: {}", e.getMessage());
            ResponseData<String> responseData = new ResponseData<>();
            responseData.setCode(e.getCode());
            responseData.setMessage(e.getMessage());
            return responseData;
        }


        return new ResponseData<LoginResponse>()
                .success(response);
    }

    @Override
    public ResponseData<?> list(CustomerRequest request) {
        String logPrefix = "danh sach khach hang ";
        ResponseData<ResponsePage<CustomerResponse>> responseData = new ResponseData<>();
        try {
            Page<User> page;

            if (request.getName() == null || request.getName().trim().isEmpty()) {
                page = userRepository.findAll(request.pageable());
            } else {
                page = userRepository.findByUsernameContainingIgnoreCase(
                        request.getName().trim(),
                        request.pageable());
            }

            List<CustomerResponse> content = page.getContent()
                    .stream()
                    .map(user -> CustomerResponse.builder()
                            .id(user.getId())
                            .name(user.getUsername())
                            .created(user.getCreatedDate())
                            .build())
                    .toList();
            responseData.success(
                    new ResponsePage<>(page, content)
            );

        } catch (Exception e) {
            LOGGER.error("xảy ra lỗi {} --- {}", logPrefix, e.getMessage());
            responseData.setCode(-1);
            responseData.setMessage("Có lỗi xảy ra");
        }
        return responseData;
    }
}
