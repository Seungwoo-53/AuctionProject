package com.auction.WebAuction.repository;

import com.auction.WebAuction.model.MemberItemBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface MemberItemBackupRepository extends JpaRepository<MemberItemBackup,Long> {

    @Query("SELECT DISTINCT mib.item.id FROM MemberItemBackup mib WHERE mib.member.id = :memberId")
    List<Long> findDistinctItemIdsByMember_Id(Long memberId);
    void deleteByItemId(Long itemId);

    @Query("SELECT mib FROM MemberItemBackup mib WHERE mib.item.id = :itemId ORDER BY mib.price DESC")
    List<MemberItemBackup> findByItemIdOrderedByPriceDesc(@Param("itemId") Long itemId);


}
