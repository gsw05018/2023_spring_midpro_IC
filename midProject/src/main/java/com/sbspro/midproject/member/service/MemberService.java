package com.sbspro.midproject.member.service;

import com.sbspro.midproject.base.app.AppConfig;
import com.sbspro.midproject.base.rsData.RsData;
import com.sbspro.midproject.base.util.Ut;
import com.sbspro.midproject.domain.attr.service.AttrService;
import com.sbspro.midproject.domain.email.service.EmailService;
import com.sbspro.midproject.domain.emailVerification.service.EmailVerificationService;
import com.sbspro.midproject.domain.genFile.entity.GenFile;
import com.sbspro.midproject.domain.genFile.service.GenFileService;
import com.sbspro.midproject.member.entity.Member;
import com.sbspro.midproject.member.repositroy.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {
    private final GenFileService genFileService;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;
    private final AttrService attrService;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;

    // 조회
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
    // 이메일을 기반으로 회원 조회

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }
    // 사용자 이름을 기반으로 회원 조회

    public Optional<Member> findById(long id) {
        return memberRepository.findById(id);
    }
    // ID를 기반으로 회원 조회

    public Optional<String> findProfileImgUrl(Member member) {
        return genFileService.findGenFileBy(
                        member.getModelName(), member.getId(), "common", "profileImg", 1
                )
                .map(GenFile::getUrl);
    }
    // 회원의 프로필 이미지 URL 조회

    public RsData<String> checkUsernameDup(String username) {
        if (findByUsername(username).isPresent()) return RsData.of("F-1", "%s(은)는 사용중인 아이디입니다.".formatted(username));

        return RsData.of("S-1", "%s(은)는 사용 가능한 아이디 입니다.".formatted(username), username);
    }
    // 아이디 중복 체크

    public RsData<String> checkEmailDup(String email) {
        if (findByEmail(email).isPresent()) return RsData.of("F-1", "%s(은)는 사용중인 이메일입니다.".formatted(email));

        return RsData.of("S-1", "%s(은)는 사용 가능한 이메일 입니다.".formatted(email), email);
    }
    // 이메일 중복 체크

    // 명령
    @Transactional
    public RsData<Member> join(String username, String password, String nickname, String email, String profileImgFilePath) {

        if (findByUsername(username).isPresent())
            return RsData.of("F-1", "%s(은)는 사용중인 아이디 입니다.".formatted(username));

        if (findByEmail(email).isPresent())
            return RsData.of("F-2", "%s(은)는 사용중인 이메일 입니다.".formatted(username));
        // 회원가입 처리

        Member member = Member
                .builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .email(email)
                .build();

        memberRepository.save(member);
        // 회원정보 저장 및 생성

        if (profileImgFilePath != null) saveProfileImg(member, profileImgFilePath);
        // 프로필 이미지 저장

        sendJoinCompleteEmail(member);
        sendEmailVerificationEmail(member);
        // 가입완료 이메일 및 이메일 인증 메일 발송

        return RsData.of("S-1", "%s님 어서오세요. 이메일 인증 후 이용해주시기 바랍니다.".formatted(username));
    }

    @Transactional
    public RsData<Member> join(String username, String password, String nickname, String email, MultipartFile profileImg) {
        String profileImgFilePath = Ut.file.toFile(profileImg, AppConfig.getTempDirPath());
        return join(username, password, nickname, email, profileImgFilePath);
    }

    private void saveProfileImg(Member member, MultipartFile profileImg) {
        if (profileImg == null) return;
        if (profileImg.isEmpty()) return;

        String profileImgFilePath = Ut.file.toFile(profileImg, AppConfig.getTempDirPath());

        saveProfileImg(member, profileImgFilePath);
    }

    private void saveProfileImg(Member member, String profileImgFilePath) {
        if (Ut.str.isBlank(profileImgFilePath)) return;

        genFileService.save(member.getModelName(), member.getId(), "common", "profileImg", 1, profileImgFilePath);
    }

    private void sendJoinCompleteEmail(Member member) {
        final String email = member.getEmail();

        CompletableFuture<RsData> sendRsFuture = emailService.send(
                email,
                "[%s 가입축하] 회원가입이 완료되었습니다.".formatted(
                        AppConfig.getSiteName()
                ),
                "많은 이용 바랍니다."
        );

        sendRsFuture.whenComplete((rs, throwable) -> {
            if (rs.isFail()) {
                log.info("sendJoinCompleteMail, 메일 발송 실패 : " + email);
                return;
            }

            log.info("sendJoinCompleteMail, 메일 발송 성공 : " + email);
        });
    }
    // 회원가입 이메일 발송 로직
    // CompletableFuture 를 사용해 비동기적으로 이메일을 발송하고, 발송 결과에 따라 로그 기록


    private void sendEmailVerificationEmail(Member member) {
        emailVerificationService.send(member);
    }
    // 이메일 인증 메일 발송 로직
    // emailVerificationService의 send 메서드를 호출해 이메일 인증 메일을 발송

    @Transactional
    public void setEmailVerified(Long memberId) {
        attrService.set("member__%d__extra__emailVerified".formatted(memberId), true);
    }
    // 이메일 인증 상태를 설정하는 로직
    // attrService의 set 메서드를 이용해 이메일 인증 상태를 true로 설정

    @Transactional
    public void setEmailVerified(Member member) {
        setEmailVerified(member.getId());
    }
    //

    public boolean isEmailVerified(Member member) {
        if (member.isSocialMember()) return true;
        return attrService.getAsBoolean("member__%d__extra__emailVerified".formatted(member.getId()), false);
    }
    // 회원이 이메일 인증 상태를 확인하는 로직
    // attrService의 getAsBoolean 메서드를 이용해 이메일 인증 상태 확인

    public Optional<Member> findByUsernameAndEmail(String username, String email) {
        return memberRepository.findByUsernameAndEmail(username, email);
    }
    // 사용자 이름과 이메일로 회원을 조회

    @Transactional
    public void sendTempPasswordToEmail(Member member) {
        final String tempPassword = Ut.str.tempPassword(8);
        // 8자리 임시 비밀번호 생성
        member.setPassword(passwordEncoder.encode(tempPassword));
        // 임시 비밀번호 암호화

        final String email = member.getEmail();

        Context context = new Context();
        context.setVariable("tempPassword", tempPassword);
        context.setVariable("siteName", AppConfig.getSiteName());
        String body = templateEngine.process("usr/member/tempPassword", context);

        CompletableFuture<RsData> sendRsFuture = emailService.send(
                email,
                "[%s] %s님 임시 비밀번호 입니다.".formatted(
                        AppConfig.getSiteName(),
                        member.getUsername()
                ),
                body
        );

        sendRsFuture.whenComplete((rs, throwable) -> {
            if (rs.isFail()) {
                log.info("sendTempPasswordToEmail, 메일 발송 실패 : " + email);
                return;
            }

            log.info("sendTempPasswordToEmail, 메일 발송 성공 : " + email);
        });
    }

    @Transactional
    public RsData<Member> modify(long memberId, String password, String nickname, MultipartFile profileImg) {

        Member member = findById(memberId).get();

        if (Ut.str.hasLength(password)) member.setPassword(passwordEncoder.encode(password));
        if (Ut.str.hasLength(nickname)) member.setNickname(nickname);
        if (profileImg != null) saveProfileImg(member, profileImg);

        return RsData.of("S-1", "회원정보가 수정되었습니다.", member);
    }
    // 회원정보 수정 기능 수행
    // 제공된 회원 ID로 조회 후 이미지, 닉네임, 비밀번호 변경
    // 변경 저장 후 성공 메시지를 포함하는 RsData 반환

    public boolean isSamePassword(Member member, String oldPassword) {
        return passwordEncoder.matches(oldPassword, member.getPassword());
    }
    // 주어진 비밀번호가 회원의 현재 비밀번호와 일치하는지 확인

    @Transactional
    public String genCheckPasswordAuthCode(Member member) {
        String code = UUID.randomUUID().toString();
        // 랜덤 인증 코드 생성

        attrService.set("member__%d__extra__checkPasswordAuthCode".formatted(member.getId()), code, LocalDateTime.now().plusSeconds(60 * 30));
        // 생성된 인증 코드를 속성 서비스에 저장

        return code; // 인증 코드 반환
    }
    // 비밀번호 확인을 위한 인증 코드를 생성하고 저장하는 기능을 수행
    // UUID.randomUUID().toString() 사용해 고유한 인증 코드 생성 후, attrService을 통해 저장

    public RsData<?> checkCheckPasswordAuthCode(Member member, String checkPasswordAuthCode) {
        if (checkPasswordAuthCode == null) return RsData.of("F-1", "checkPasswordAuthCode를 입력해주세요.");

        if (attrService.get("member__%d__extra__checkPasswordAuthCode".formatted(member.getId()), "").equals(checkPasswordAuthCode))
            return RsData.of("S-1", "유효한 코드입니다.");

        return RsData.of("F-2", "유효하지 않은 코드입니다.");
    }
    // 제공된 인증 코드가 유효한지 확인하는 기능을 수행
    // attrService.get을 사용하여 저장된 인증 코드를 조회하고, 입력된 코드와 비교

    public Page<Member> findByKw(String kwType, String kw, Pageable pageable) {
        return memberRepository.findByKw(kwType, kw, pageable);
    }
    // 키워드 유형, 키워드 , 페이지 정보를 기반으로 회원 조회
    // 페이지네이션과 함꼐 반환

    public String getProfileImgUrl(Member member) {
        return Optional.ofNullable(member)
                .flatMap(this::findProfileImgUrl)
                .orElse("/image/default_profileImg.png");
    }
    // 프로필 이미지 URL 반환
    // 이미지가 없을경우 기본 이미지 URL 반환

    @Transactional
    public Member whenSocialLogin(String providerTypeCode, String username, String nickname, String profileImgUrl) {
        Optional<Member> opMember = findByUsername(username);

        if (opMember.isPresent()) return opMember.get();

        String filePath = Ut.str.hasLength(profileImgUrl) ? Ut.file.downloadFileByHttp(profileImgUrl, AppConfig.getTempDirPath()) : "";

        return join(username, "", nickname, "", filePath).getData();
    }

}