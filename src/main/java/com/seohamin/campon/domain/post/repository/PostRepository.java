package com.seohamin.campon.domain.post.repository;

import com.seohamin.campon.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByCampsiteIdOrderByCreatedAtDesc(Long campsiteId, Pageable pageable);
}
