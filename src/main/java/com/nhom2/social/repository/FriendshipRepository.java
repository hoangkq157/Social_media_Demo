package com.nhom2.social.repository;

import com.nhom2.social.entity.Friendship;
import com.nhom2.social.entity.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository truy van bang friendships:
 * - Tim loi moi ket ban theo sender va receiver
 * - Lay danh sach loi moi dang cho (PENDING)
 * - Lay danh sach ban be da xac nhan (ACCEPTED)
 */
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Optional<Friendship> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    @Query("SELECT f FROM Friendship f WHERE (f.sender.id = :u1 AND f.receiver.id = :u2) OR (f.sender.id = :u2 AND f.receiver.id = :u1)")
    Optional<Friendship> findRelationBetween(@Param("u1") Long u1, @Param("u2") Long u2);

    @Query("SELECT f FROM Friendship f JOIN FETCH f.sender WHERE f.receiver.id = :receiverId AND f.status = :status ORDER BY f.createdAt DESC")
    List<Friendship> findByReceiverIdAndStatusWithSender(@Param("receiverId") Long receiverId, @Param("status") FriendshipStatus status);

    @Query("SELECT f FROM Friendship f JOIN FETCH f.receiver WHERE f.sender.id = :senderId AND f.status = :status ORDER BY f.createdAt DESC")
    List<Friendship> findBySenderIdAndStatusWithReceiver(@Param("senderId") Long senderId, @Param("status") FriendshipStatus status);

    @Query("SELECT f FROM Friendship f JOIN FETCH f.sender JOIN FETCH f.receiver " +
           "WHERE (f.sender.id = :userId OR f.receiver.id = :userId) AND f.status = 'ACCEPTED' " +
           "ORDER BY f.updatedAt DESC")
    List<Friendship> findAllAcceptedFriends(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM Friendship f WHERE (f.sender.id = :userId OR f.receiver.id = :userId) AND f.status = 'ACCEPTED'")
    long countAcceptedFriends(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM Friendship f WHERE f.receiver.id = :receiverId AND f.status = 'PENDING'")
    long countPendingReceived(@Param("receiverId") Long receiverId);

    @Query("SELECT CASE WHEN f.sender.id = :userId THEN f.receiver.id ELSE f.sender.id END FROM Friendship f " +
           "WHERE (f.sender.id = :userId OR f.receiver.id = :userId) AND f.status = 'ACCEPTED'")
    List<Long> findFriendUserIds(@Param("userId") Long userId);
}
