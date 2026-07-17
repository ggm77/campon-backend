package com.seohamin.campon.domain.post.service;

import com.seohamin.campon.domain.post.dto.PostRequestDto;
import com.seohamin.campon.domain.post.dto.PostResponseDto;
import com.seohamin.campon.domain.post.entity.Post;
import com.seohamin.campon.domain.post.repository.PostRepository;
import com.seohamin.campon.domain.user.entity.User;
import com.seohamin.campon.domain.user.repository.UserRepository;
import com.seohamin.campon.global.dto.PageNationDto;
import com.seohamin.campon.global.exception.CustomException;
import com.seohamin.campon.global.exception.constants.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 커뮤니티 게시글 등록시키는 메서드
     * @param userIdStr 요청 유저 id
     * @param postRequestDto 게시글 정보 담긴 DTO
     * @return 등록된 게시글의 정보
     */
    @Transactional
    public PostResponseDto createPost(
            final String userIdStr,
            final PostRequestDto postRequestDto
    ) {
        // 1) null 검사
        if (
                userIdStr == null || userIdStr.isBlank()
                || postRequestDto == null
                || postRequestDto.campsiteId() == null
                || postRequestDto.title() == null || postRequestDto.title().isBlank()
                || postRequestDto.content() == null || postRequestDto.content().isBlank()
        ) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        // 2) 파싱
        final Long userId = Long.parseLong(userIdStr);

        // 3) 유저 조회
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_EXIST));

        // 4) 게시물 객체 생성
        final Post post = Post.builder()
                .author(user)
                .campsiteId(postRequestDto.campsiteId())
                .title(postRequestDto.title())
                .content(postRequestDto.content())
                .build();

        // 5) 저장
        final Post savedPost = postRepository.save(post);

        return PostResponseDto.of(savedPost);
    }

    /**
     * 게시글 하나 조회하는 메서드
     * @param postId 조회할 게시글 id
     * @return 조회한 게시글 정보
     */
    @Transactional(readOnly = true)
    public PostResponseDto getPost(
            final Long postId
    ) {
        // 1) null 검사
        if  (postId == null) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        // 2) 게시글 조회
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_EXIST));

        return PostResponseDto.of(post);
    }

    /**
     * 특정 캠핑장 게시글 전체 조회 API
     * @param campsiteId 캠핑장 id
     * @param size 페이지 크기
     * @param page 페이지 번호 (0번부터)
     * @return 조회된 게시글들
     */
    @Transactional(readOnly = true)
    public PageNationDto<PostResponseDto> getAllPosts(
            final Long campsiteId,
            final Integer size,
            final Integer page
    ) {
        // 1) null 검사
        if (campsiteId == null ||  size == null || page == null) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        // 2) 페이지네이션 검사
        if (size > 1000 || size < 1 || page < 0) {
            throw new CustomException(ExceptionCode.INVALID_PAGING_PARAMETER);
        }

        // 3) 게시글 조회
        final Page<Post> postPage = postRepository.findByCampsiteIdOrderByCreatedAtDesc(
                campsiteId,
                PageRequest.of(page, size)
        );

        // 4) DTO 변환
        final List<PostResponseDto> content = postPage.getContent().stream()
                .map(PostResponseDto::of)
                .toList();

        return new PageNationDto<>(postPage.hasNext(), content);
    }

    /**
     * 게시글 수정하는 메서드
     * @param userIdStr 요청 유저 id
     * @param postId 수정할 게시글 id
     * @param postRequestDto 수정할 정보
     * @return 수정된 게시글
     */
    @Transactional
    public PostResponseDto updatePost(
            final String userIdStr,
            final Long postId,
            final PostRequestDto postRequestDto
    ) {
        // 1) null 검사
        if (
                userIdStr == null || userIdStr.isBlank()
                || postId == null
                || postRequestDto == null
        ) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        // 2) 파싱
        final Long userId = Long.parseLong(userIdStr);

        // 3) 게시글 조회
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_EXIST));

        // 4) 작성자 검사
        if (!post.getAuthor().getId().equals(userId)) {
            throw new CustomException(ExceptionCode.FORBIDDEN_USER_RESOURCE_ACCESS);
        }

        // 5) 게시글 수정
        if (postRequestDto.campsiteId() != null) {
            post.updateCampsiteId(postRequestDto.campsiteId());
        }

        if (postRequestDto.title() != null && !postRequestDto.title().isBlank()) {
            post.updateTitle(postRequestDto.title());
        }

        if (postRequestDto.content() != null && !postRequestDto.content().isBlank()) {
            post.updateContent(postRequestDto.content());
        }

        return PostResponseDto.of(post);
    }

    /**
     * 게시글 삭제 하는 메서드
     * @param userIdStr 요청 유저 id
     * @param postId 삭제할 게시글 id
     */
    public void deletePost(
            final String userIdStr,
            final Long postId
    ) {
        // 1) null 검사
        if (
                userIdStr == null || userIdStr.isBlank()
                || postId == null
        ) {
            throw new CustomException(ExceptionCode.INVALID_REQUEST);
        }

        // 2) 파싱
        final Long userId = Long.parseLong(userIdStr);

        // 3) 게시글 조회
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ExceptionCode.POST_NOT_EXIST));

        // 4) 작성자 검사
        if (!post.getAuthor().getId().equals(userId)) {
            throw new CustomException(ExceptionCode.FORBIDDEN_USER_RESOURCE_ACCESS);
        }

        postRepository.delete(post);
    }
}
