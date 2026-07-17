package com.seohamin.campon.domain.post.controller;

import com.seohamin.campon.domain.post.dto.PostRequestDto;
import com.seohamin.campon.domain.post.dto.PostResponseDto;
import com.seohamin.campon.domain.post.service.PostService;
import com.seohamin.campon.global.dto.PageNationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PostController {

    private final PostService postService;

    // 커뮤니티 게시글 작성하는 API
    @PostMapping("/posts")
    public ResponseEntity<PostResponseDto> createPost(
            @AuthenticationPrincipal String userIdStr,
            @RequestBody final PostRequestDto postRequestDto
    ) {

        return ResponseEntity.ok(postService.createPost(userIdStr, postRequestDto));
    }

    // 게시글 하나 조회 API
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponseDto> getPost(
            @PathVariable final Long id
    ) {

        return ResponseEntity.ok(postService.getPost(id));
    }

    // 특정 캠핑장의 게시글 전체 조회 API
    @GetMapping("/posts")
    public ResponseEntity<PageNationDto<PostResponseDto>> getAllPosts(
            @RequestParam final Long campsiteId,
            @RequestParam final Integer size,
            @RequestParam final Integer page
    ) {

        return ResponseEntity.ok(postService.getAllPosts(campsiteId, size, page));
    }

    // 게시글 수정 API
    @PatchMapping("/posts/{id}")
    public ResponseEntity<PostResponseDto> updatePost(
            @AuthenticationPrincipal final String userIdStr,
            @PathVariable final Long id,
            @RequestBody final PostRequestDto postRequestDto
    ) {

        return ResponseEntity.ok(postService.updatePost(userIdStr, id, postRequestDto));
    }

    // 게시글 삭제 API
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal final String userIdStr,
            @PathVariable final Long id
    ) {
        postService.deletePost(userIdStr, id);

        return ResponseEntity.noContent().build();
    }
}
