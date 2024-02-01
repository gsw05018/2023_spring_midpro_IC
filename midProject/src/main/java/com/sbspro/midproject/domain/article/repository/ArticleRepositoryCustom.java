package com.sbspro.midproject.domain.article.repository;

import com.sbspro.midproject.domain.article.entity.Article;
import com.sbspro.midproject.domain.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleRepositoryCustom {
    Page<Article> findByKw(Board board, String kwType, String kw, Pageable pageable);
}