package com.auction.WebAuction.validator;

import com.auction.WebAuction.dto.MemberRegisterDTO;
import com.auction.WebAuction.repository.MemberFormRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@RequiredArgsConstructor
@Component
public class CheckUserIdValidator extends AbstractValidator<MemberRegisterDTO>{
    private final MemberFormRepository memberFormRepository;
    @Override
    protected void doValidate(MemberRegisterDTO memberRegisterDTO, Errors errors) {
        if(memberFormRepository.existByUserId(memberRegisterDTO.getUsername())){
            errors.rejectValue("username", "아이디 중복 오류", "이미 사용중인 아이디 입니다.");
        }
    }
}
