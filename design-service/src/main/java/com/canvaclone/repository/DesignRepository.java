package com.canvaclone.repository;

import com.canvaclone.entity.Design;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesignRepository extends JpaRepository<Design, Long> {
    List<Design> findAllByOwnerEmailOrderByUpdatedAtDesc(String ownerEmail);
    List<Design> findAllByIsPublicTrueOrderByUpdatedAtDesc();
}
