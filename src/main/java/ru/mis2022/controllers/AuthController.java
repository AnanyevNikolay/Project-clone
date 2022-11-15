package ru.mis2022.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mis2022.config.security.jwt.JwtResponse;
import ru.mis2022.config.security.jwt.JwtUtils;
import ru.mis2022.config.security.jwt.LoginRequest;
import ru.mis2022.models.dto.user.UserDto;
import ru.mis2022.models.dto.user.UserPasswordChangingDto;
import ru.mis2022.models.dto.user.converter.UserDtoConverter;
import ru.mis2022.models.entity.Invite;
import ru.mis2022.models.entity.User;
import ru.mis2022.models.response.Response;
import ru.mis2022.service.entity.InviteService;
import ru.mis2022.service.entity.UserService;
import ru.mis2022.utils.validation.ApiValidationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final InviteService inviteService;
    private final UserService userService;
    private final UserDtoConverter userDtoConverter;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                roles));
    }

    @ApiOperation("Проверяем код активации и устанавливаем пароль пользователю")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Пароль установлен"),
            @ApiResponse(code = 410, message = "Пароль менее 10 символов"),
            @ApiResponse(code = 415, message = "Ссылка устарела"),
    })
    @PostMapping("/confirm/emailpassword")
    public Response<UserDto> confirmEmailPassword(@RequestBody UserPasswordChangingDto userPasswordChangingDto) {
        String pwd = userPasswordChangingDto.password().trim();
        ApiValidationUtils.expectedFalse(pwd.length() < 10, 410, "Пароль менее 10 символов");
        Invite invite = inviteService.findByToken(userPasswordChangingDto.token());
        ApiValidationUtils.expectedFalse(invite == null || invite.getExpirationDate().isBefore(LocalDateTime.now()),
                415, "Ссылка устарела");
        User user = userService.findUserById(invite.getId());
        user = userService.saveUserByInviteAndDeleteInvite(user, pwd, invite);
        return Response.ok(userDtoConverter.toDto(user));
    }

}
