package com.example.weuniteauth.mapper;

import com.example.weuniteauth.dto.auth.resetpassword.ResetPasswordResponseDTO;
import com.example.weuniteauth.dto.auth.resetpassword.SendResetPasswordResponseDTO;
import com.example.weuniteauth.dto.auth.resetpassword.VerifyResetTokenResponseDTO;
import com.example.weuniteauth.dto.auth.verifyemail.VerifyEmailResponseDTO;
import com.example.weuniteauth.dto.auth.login.LoginResponseDTO;
import com.example.weuniteauth.dto.auth.signup.SignUpResponseDTO;
import com.example.weuniteauth.dto.common.ExtendedTokenResponseDTO;
import com.example.weuniteauth.dto.common.MessageResponseDTO;
import com.example.weuniteauth.dto.common.TokenResponseDTO;
import com.example.weuniteauth.dto.common.UserBaseDTO;
import com.example.weuniteauth.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {


    TokenResponseDTO toTokenResponseDTO(String accessToken, Long expiresIn);

    default LoginResponseDTO toLoginResponseDTO(String accessToken, Long expiresIn) {
        TokenResponseDTO tokenResponse = toTokenResponseDTO(accessToken, expiresIn);
        return LoginResponseDTO.from(tokenResponse);
    }

    UserBaseDTO toUserBaseDTO(User user);

    default SignUpResponseDTO toSignUpResponseDTO(User user) {
        UserBaseDTO userBase = toUserBaseDTO(user);
        return SignUpResponseDTO.from(userBase);
    }


    ExtendedTokenResponseDTO toExtendedTokenResponseDTO(String username, boolean verified, String message, String accessToken, Long expiresIn);

    default VerifyEmailResponseDTO toVerifyEmailResponseDTO(String username, boolean verified, String message, String accessToken, Long expiresIn) {
        ExtendedTokenResponseDTO extendedResponse = toExtendedTokenResponseDTO(username, verified, message, accessToken, expiresIn);
        return VerifyEmailResponseDTO.from(extendedResponse);
    }

    MessageResponseDTO toMessageResponseDTO(String message);

    default SendResetPasswordResponseDTO toSendResetPasswordResponseDTO(String message) {
        MessageResponseDTO messageResponse = toMessageResponseDTO(message);
        return SendResetPasswordResponseDTO.from(messageResponse);
    }

    default VerifyResetTokenResponseDTO toVerifyResetTokenResponseDTO(String message) {
        MessageResponseDTO messageResponse = toMessageResponseDTO(message);
        return VerifyResetTokenResponseDTO.from(messageResponse);
    }

    default ResetPasswordResponseDTO toResetPasswordResponseDTO(String message) {
        MessageResponseDTO messageResponse = toMessageResponseDTO(message);
        return ResetPasswordResponseDTO.from(messageResponse);
    }
}
