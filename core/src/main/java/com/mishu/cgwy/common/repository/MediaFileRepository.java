package com.mishu.cgwy.common.repository;

import com.mishu.cgwy.common.domain.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * User: xudong
 * Date: 12/2/14
 * Time: 3:26 PM
 */
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    List<MediaFile> findByQiNiuHash(String hash);
}
