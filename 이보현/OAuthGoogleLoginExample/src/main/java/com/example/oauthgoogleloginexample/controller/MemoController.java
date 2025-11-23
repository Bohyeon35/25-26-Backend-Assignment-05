package com.example.oauthgoogleloginexample.controller;

import com.example.oauthgoogleloginexample.dto.MemoRequestDto;
import com.example.oauthgoogleloginexample.dto.MemoResponseDto;
import com.example.oauthgoogleloginexample.dto.MemoUpdateRequestDto;
import com.example.oauthgoogleloginexample.service.MemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/memos")
public class MemoController {

    private final MemoService memoService;

    @PostMapping
    public ResponseEntity<MemoResponseDto> saveMemo(Principal principal, @RequestBody MemoRequestDto memoRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memoService.createMemo(principal, memoRequestDto));
    }

    @GetMapping("/{memoId}")
    public ResponseEntity<MemoResponseDto> getMemo(@PathVariable Long memoId) {
        return ResponseEntity.status(HttpStatus.OK).body(memoService.getMemo(memoId));
    }

    @PatchMapping("/{memoId}")
    public ResponseEntity<MemoResponseDto> updateMemo(@PathVariable Long memoId,
                                         @RequestBody MemoUpdateRequestDto memoUpdateRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(memoService.updateMemo(memoId,memoUpdateRequestDto));
    }

    @DeleteMapping("/{memoId}")
    public ResponseEntity<MemoResponseDto> deleteMemo(@PathVariable Long memoId) {
        memoService.deleteMemo(memoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MemoResponseDto>> getAllMemo() {
        return ResponseEntity.status(HttpStatus.OK).body(memoService.getAllMemo());
    }
}
