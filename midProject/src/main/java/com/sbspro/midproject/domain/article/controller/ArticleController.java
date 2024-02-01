package com.sbspro.midproject.domain.article.controller;

import com.sbspro.midproject.base.exception.NeedHistoryBackException;
import com.sbspro.midproject.base.rq.Rq;
import com.sbspro.midproject.base.rsData.RsData;
import com.sbspro.midproject.domain.article.entity.Article;
import com.sbspro.midproject.domain.article.service.ArticleService;
import com.sbspro.midproject.domain.board.entity.Board;
import com.sbspro.midproject.domain.board.service.BoardService;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/usr/article")
@RequiredArgsConstructor
public class ArticleController {
    private final BoardService boardService;
    private final ArticleService articleService;
    private final Rq rq;

    @GetMapping("/{boardCode}/list")
    public String showList(
            Model model,
            @PathVariable String boardCode,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String kw,
            @RequestParam(defaultValue = "all") String kwType
    ) {
        Board board = boardService.findByCode(boardCode).get();

        model.addAttribute("board", board);

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        Page<Article> articlePage = articleService.findByKw(board, kwType, kw, pageable);
        model.addAttribute("articlePage", articlePage);

        return "usr/article/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{boardCode}/write")
    public String showWrite(
            Model model,
            @PathVariable String boardCode
    ) {
        Board board = boardService.findByCode(boardCode).get();

        model.addAttribute("board", board);

        return "usr/article/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{boardCode}/write")
    public String write(
            Model model,
            @PathVariable String boardCode,
            ArticleWriteForm articleWriteForm
    ) {
        Board board = boardService.findByCode(boardCode).get();

        RsData<Article> rsData = articleService.write(board, rq.getMember(), articleWriteForm.getSubject(), articleWriteForm.getBody());

        return rq.redirectOrBack("/usr/article/%s/detail/%d".formatted(board.getCode(), rsData.getData().getId()), rsData);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{boardCode}/detail/{id}")
    public String showDetail(
            Model model,
            @PathVariable String boardCode,
            @PathVariable long id
    ) {
        Board board = boardService.findByCode(boardCode).get();
        Article article = articleService.findById(id).get();

        model.addAttribute("board", board);
        model.addAttribute("article", article);

        return "usr/article/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{boardCode}/modify/{id}")
    public String showModify(Model model, @PathVariable String boardCode, @PathVariable long id) {
        Board board = boardService.findByCode(boardCode).get();
        Article article = articleService.findById(id).get();

        articleService
                .checkActorCanModify(rq.getMember(), article)
                .optional()
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new NeedHistoryBackException(rsData);
                });

        model.addAttribute("board", board);
        model.addAttribute("article", article);

        return "usr/article/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{boardCode}/modify/{id}")
    public String modify(Model model, @PathVariable String boardCode, @PathVariable long id, ArticleModifyForm articleModifyForm) {
        Board board = boardService.findByCode(boardCode).get();
        Article article = articleService.findById(id).get();

        articleService
                .checkActorCanModify(rq.getMember(), article)
                .optional()
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new NeedHistoryBackException(rsData);
                });

        RsData<Article> rsData = articleService.modify(article, articleModifyForm.getSubject(), articleModifyForm.getBody());

        return rq.redirectOrBack("/usr/article/%s/detail/%d".formatted(board.getCode(), rsData.getData().getId()), rsData);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{boardCode}/remove/{id}")
    public String remove(
            @PathVariable String boardCode,
            @PathVariable long id
    ) {
        Board board = boardService.findByCode(boardCode).get();
        Article article = articleService.findById(id).get();

        articleService
                .checkActorCanDelete(rq.getMember(), article)
                .optional()
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new NeedHistoryBackException(rsData);
                });

        RsData<?> rsData = articleService.remove(article);

        return rq.redirectOrBack("/usr/article/%s/list".formatted(board.getCode()), rsData);
    }

    @AllArgsConstructor
    @Getter
    public static class ArticleModifyForm {
        @NotBlank
        private String subject;

        @NotBlank
        private String body;
    }

    @AllArgsConstructor
    @Getter
    public static class ArticleWriteForm {
        private String subject;
        private String body;
    }

}