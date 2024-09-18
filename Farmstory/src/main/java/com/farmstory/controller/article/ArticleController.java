package com.farmstory.controller.article;

import com.farmstory.dto.ArticleDTO;
import com.farmstory.dto.CateDTO;
import com.farmstory.entity.Article;
import com.farmstory.service.ArticleService;
import com.farmstory.service.CategoryService;
import com.farmstory.service.PageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final PageService pageService;


    @GetMapping("/{cateGroup}/{cateName}")
    public String list(@PathVariable("cateGroup") String group,
                           @PathVariable("cateName") String cateName,
                           @RequestParam(value = "content", required = false) String content,
                           @RequestParam(value ="page", defaultValue = "0") int page,
                           Model model){

        CateDTO cate = categoryService.selectCategory(group,cateName);
        int cateNo = cate.getCateNo();
        //300번대 = croptalk ,  500대 = community
        List<ArticleDTO> articles = null;
        if ((cateNo >= 300 && cateNo < 400) || (cateNo >= 500 && cateNo < 600)) {

            Pageable pageable = PageRequest.of(page, 10);
            Page<Article> articlePage = pageService.getArticles(cateNo, pageable);
            articles = articlePage.getContent().stream().map(article -> article.toDTO()).toList();
            log.info(articles);

            int totalPages = articlePage.getTotalPages();
            int currentPage = page;

            log.info(page);
            // 페이지 네비게이션 범위 계산 (한 번에 5개 페이지 링크 표시)
            int startPage = Math.max(0, currentPage - 2); // 현재 페이지에서 앞쪽 2개까지 표시
            int endPage = Math.max(0, Math.min(totalPages - 1, currentPage + 2));

            log.info("startpage : "+startPage);
            log.info("endpage : "+endPage);

            model.addAttribute("cate", cate);
            model.addAttribute("content", content);
            model.addAttribute("articles",articles);

            model.addAttribute("currentPage", currentPage);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            model.addAttribute("totalPages", totalPages);

        }


        return "boardIndex";

    }

    @GetMapping("/{cateGroup}/{cateName}/{articleNo}")
    public String list(@PathVariable("cateGroup") String group,
                       @PathVariable("cateName") String cateName,
                       @PathVariable("articleNo") int articleNo,
                       @RequestParam(value = "content", required = false) String content,
                       Model model){

        CateDTO cate = categoryService.selectCategory(group,cateName);

       ArticleDTO articleDTO =  articleService.selectArticle(articleNo);
       model.addAttribute("cate", cate);
       model.addAttribute("content", content);
       model.addAttribute("article", articleDTO);
        return "boardIndex";
    }








}