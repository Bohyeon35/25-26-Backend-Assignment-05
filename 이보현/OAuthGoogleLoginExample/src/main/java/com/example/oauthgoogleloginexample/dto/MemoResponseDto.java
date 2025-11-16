package com.example.oauthgoogleloginexample.dto;

import com.example.oauthgoogleloginexample.domain.Memo;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemoResponseDto {
    private long id;
    private String title;
    private String content;
    private Long userId;
    private String userName;

    public static MemoResponseDto from(Memo memo) {
        return MemoResponseDto.builder()
                .id(memo.getId())
                .title(memo.getTitle())
                .content(memo.getContent())
                .userId(memo.getUser().getId())
                .userName(memo.getUser().getName())
                .build();
    }
}
