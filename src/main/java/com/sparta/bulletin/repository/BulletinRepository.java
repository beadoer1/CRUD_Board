package com.sparta.bulletin.repository;

import com.sparta.bulletin.model.Bulletin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BulletinRepository extends JpaRepository<Bulletin, Long> {
    List<Bulletin> findAllByOrderByModifiedAtDesc();
}
