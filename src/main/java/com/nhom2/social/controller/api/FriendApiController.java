package com.nhom2.social.controller.api;

import com.nhom2.social.dto.FriendRelation;
import com.nhom2.social.dto.FriendStatusResponse;
import com.nhom2.social.service.FriendshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API xu ly cac thao tac ket ban qua AJAX:
 * - Gui loi moi ket ban
 * - Chap nhan loi moi
 * - Tu choi / Huy loi moi
 * - Huy ket ban (Unfriend)
 */
@RestController
@RequestMapping("/api/friends")
public class FriendApiController {

    private final FriendshipService friendshipService;

    public FriendApiController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    /**
     * Gui loi moi ket ban toi targetUsername.
     */
    @PostMapping("/request/{targetUsername}")
    public ResponseEntity<FriendStatusResponse> sendRequest(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable("targetUsername") String targetUsername) {
        String currentUsername = principal.getUsername();
        FriendRelation relation = friendshipService.sendRequest(currentUsername, targetUsername);
        long friendCount = friendshipService.countFriends(targetUsername);

        String msg = relation == FriendRelation.FRIENDS ? "Đã trở thành bạn bè" : "Đã gửi lời mời kết bạn";
        return ResponseEntity.ok(new FriendStatusResponse(relation.name(), friendCount, msg));
    }

    /**
     * Chap nhan loi moi ket ban tu senderUsername.
     */
    @PostMapping("/accept/{senderUsername}")
    public ResponseEntity<FriendStatusResponse> acceptRequest(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable("senderUsername") String senderUsername) {
        String currentUsername = principal.getUsername();
        friendshipService.acceptRequest(currentUsername, senderUsername);
        long friendCount = friendshipService.countFriends(currentUsername);

        return ResponseEntity.ok(new FriendStatusResponse(FriendRelation.FRIENDS.name(), friendCount, "Đã chấp nhận lời mời kết bạn"));
    }

    /**
     * Tu choi / xoa loi moi ket ban tu senderUsername.
     */
    @PostMapping("/reject/{senderUsername}")
    public ResponseEntity<FriendStatusResponse> rejectRequest(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable("senderUsername") String senderUsername) {
        String currentUsername = principal.getUsername();
        friendshipService.rejectRequest(currentUsername, senderUsername);
        long friendCount = friendshipService.countFriends(currentUsername);

        return ResponseEntity.ok(new FriendStatusResponse(FriendRelation.NONE.name(), friendCount, "Đã từ chối lời mời kết bạn"));
    }

    /**
     * Huy loi moi da gui toi targetUsername.
     */
    @PostMapping("/cancel/{targetUsername}")
    public ResponseEntity<FriendStatusResponse> cancelRequest(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable("targetUsername") String targetUsername) {
        String currentUsername = principal.getUsername();
        friendshipService.cancelRequest(currentUsername, targetUsername);
        long friendCount = friendshipService.countFriends(targetUsername);

        return ResponseEntity.ok(new FriendStatusResponse(FriendRelation.NONE.name(), friendCount, "Đã hủy lời mời kết bạn"));
    }

    /**
     * Huy ket ban voi targetUsername.
     */
    @PostMapping("/unfriend/{targetUsername}")
    public ResponseEntity<FriendStatusResponse> unfriend(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable("targetUsername") String targetUsername) {
        String currentUsername = principal.getUsername();
        friendshipService.unfriend(currentUsername, targetUsername);
        long friendCount = friendshipService.countFriends(targetUsername);

        return ResponseEntity.ok(new FriendStatusResponse(FriendRelation.NONE.name(), friendCount, "Đã hủy kết bạn"));
    }
}
