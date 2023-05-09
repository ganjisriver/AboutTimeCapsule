package com.timecapsule.oauthservice.controller;

import com.timecapsule.oauthservice.api.response.CommonRes;
import com.timecapsule.oauthservice.api.response.MemberRes;
import com.timecapsule.oauthservice.api.response.SuccessRes;
import com.timecapsule.oauthservice.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
@RestController
public class MemberController {
    private final MemberService memberService;
    @GetMapping("/me")
    public SuccessRes<MemberRes> getUser() {
        log.info("내 회원정보 조회");
        return memberService.getMemberInfo();
    }

    @PutMapping ("/nickname/{nickname}")
    public CommonRes updateNickname(@PathVariable String nickname){
        String encodedNickname = URLDecoder.decode(nickname, StandardCharsets.UTF_8);
        return memberService.updateNickname(encodedNickname);
    }

    @GetMapping("/nickname/{nickname}/exists")
    public SuccessRes checkNicknameDuplicate(@PathVariable String nickname){
        String encodedNickname = URLDecoder.decode(nickname, StandardCharsets.UTF_8);
        return memberService.checkNicknameDuplicate(encodedNickname);
    }
}
