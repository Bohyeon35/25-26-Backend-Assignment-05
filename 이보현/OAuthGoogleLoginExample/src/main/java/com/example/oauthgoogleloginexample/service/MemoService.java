package com.example.oauthgoogleloginexample.service;

import com.example.oauthgoogleloginexample.domain.Memo;
import com.example.oauthgoogleloginexample.domain.User;
import com.example.oauthgoogleloginexample.dto.MemoRequestDto;
import com.example.oauthgoogleloginexample.dto.MemoResponseDto;
import com.example.oauthgoogleloginexample.dto.MemoUpdateRequestDto;
import com.example.oauthgoogleloginexample.repository.MemoRepository;
import com.example.oauthgoogleloginexample.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoService {

    private final MemoRepository memoRepository;
    private final UserRepository userRepository;

    @Transactional
    public MemoResponseDto createMemo(Principal principal, MemoRequestDto memoRequestDto) {
        User user = userRepository.findById(Long.parseLong(principal.getName()))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Memo memo = Memo.builder()
                .title(memoRequestDto.getTitle())
                .content(memoRequestDto.getContent())
                .user(user)
                .build();

        memoRepository.save(memo);

        return MemoResponseDto.from(memo);
    }

    @Transactional(readOnly = true)
    public MemoResponseDto getMemo(Long memoId) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("요청하신 메모 정보를 찾을 수 없습니다."));

        return MemoResponseDto.from(memo);
    }

    @Transactional
    public MemoResponseDto updateMemo(Long memoId, MemoUpdateRequestDto memoUpdateRequestDto) {
        Memo memo = memoRepository.findById(memoId)
                .orElseThrow(() -> new IllegalArgumentException("요청하신 메모 정보를 찾을 수 없습니다."));

        memo.update(memoUpdateRequestDto.getTitle(), memoUpdateRequestDto.getContent());

        return MemoResponseDto.from(memo);
    }

    @Transactional
    public void deleteMemo(Long memoId) {
        memoRepository.deleteById(memoId);
    }

    @Transactional(readOnly = true)
    public List<MemoResponseDto> getAllMemo() {
        return memoRepository.findAll()
                .stream()
                .map(MemoResponseDto::from)
                .toList();
    }

}
