package com.example.oauthgoogleloginexample.repository;

import com.example.oauthgoogleloginexample.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoRepository extends JpaRepository<Memo, Long> {
}
