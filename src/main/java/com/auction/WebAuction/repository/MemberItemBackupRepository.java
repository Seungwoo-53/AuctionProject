package com.auction.WebAuction.repository;

import com.auction.WebAuction.model.MemberItemBackup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberItemBackupRepository extends JpaRepository<MemberItemBackup,Long> {

    List<MemberItemBackup> findByMember_Id(Long memberId);
}
