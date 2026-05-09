package com.canvaclone.repository;

import com.canvaclone.entity.Design;
import com.canvaclone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesignRepository extends JpaRepository<Design, Long> {
    List<Design> findAllByUserOrderByUpdatedAtDesc(User user);
    List<Design> findAllByIsPublicTrueOrderByUpdatedAtDesc();
}
