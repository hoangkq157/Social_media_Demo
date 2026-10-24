package com.nhom2.social.service;

import com.nhom2.social.dto.FriendRelation;
import com.nhom2.social.dto.FriendRequestView;
import com.nhom2.social.dto.UserCard;
import com.nhom2.social.entity.Friendship;
import com.nhom2.social.entity.FriendshipStatus;
import com.nhom2.social.entity.User;
import com.nhom2.social.exception.NotFoundException;
import com.nhom2.social.repository.FriendshipRepository;
import com.nhom2.social.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service xu ly nghiep vu ket ban:
 * - Gui loi moi ket ban
 * - Chap nhan loi moi
 * - Tu choi / Huy loi moi
 * - Huy ket ban (Unfriend)
 * - Lay danh sach loi moi cho duyet va danh sach ban be
 */
@Service
@Transactional(readOnly = true)
public class FriendshipService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public FriendshipService(FriendshipRepository friendshipRepository,
                             UserRepository userRepository,
                             UserService userService) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Gui loi moi ket ban tu currentUsername toi targetUsername.
     */
    @Transactional
    public FriendRelation sendRequest(String currentUsername, String targetUsername) {
        if (currentUsername == null || targetUsername == null) {
            throw new IllegalArgumentException("Username không hợp lệ");
        }
        if (currentUsername.equalsIgnoreCase(targetUsername)) {
            throw new IllegalArgumentException("Không thể tự kết bạn với chính mình");
        }

        User sender = getUserByUsername(currentUsername);
        User receiver = getUserByUsername(targetUsername);

        Optional<Friendship> opt = friendshipRepository.findRelationBetween(sender.getId(), receiver.getId());
        if (opt.isPresent()) {
            Friendship f = opt.get();
            if (f.getStatus() == FriendshipStatus.ACCEPTED) {
                return FriendRelation.FRIENDS;
            }
            if (f.getStatus() == FriendshipStatus.PENDING) {
                // Neu nguoi kia da tung gui loi moi truoc do -> tu dong chap nhan thanh ban be
                if (f.getSender().getId().equals(receiver.getId())) {
                    f.setStatus(FriendshipStatus.ACCEPTED);
                    friendshipRepository.save(f);
                    return FriendRelation.FRIENDS;
                }
                return FriendRelation.PENDING_SENT;
            }
            // Neu truoc do bi REJECTED thi chuyen lai sang PENDING voi sender moi
            f.setSender(sender);
            f.setReceiver(receiver);
            f.setStatus(FriendshipStatus.PENDING);
            friendshipRepository.save(f);
            return FriendRelation.PENDING_SENT;
        }

        Friendship friendship = new Friendship(sender, receiver, FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
        return FriendRelation.PENDING_SENT;
    }

    /**
     * Chap nhan loi moi ket ban ma nguoi khac gui cho currentUsername.
     */
    @Transactional
    public void acceptRequest(String currentUsername, String senderUsername) {
        User receiver = getUserByUsername(currentUsername);
        User sender = getUserByUsername(senderUsername);

        Friendship friendship = friendshipRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy lời mời kết bạn từ @" + senderUsername));

        if (friendship.getStatus() == FriendshipStatus.ACCEPTED) {
            return;
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);
    }

    /**
     * Tu choi / xoa loi moi ket ban nhan duoc tu senderUsername.
     */
    @Transactional
    public void rejectRequest(String currentUsername, String senderUsername) {
        User receiver = getUserByUsername(currentUsername);
        User sender = getUserByUsername(senderUsername);

        Friendship friendship = friendshipRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy lời mời kết bạn từ @" + senderUsername));

        friendshipRepository.delete(friendship);
    }

    /**
     * Huy loi moi ket ban da gui toi targetUsername.
     */
    @Transactional
    public void cancelRequest(String currentUsername, String targetUsername) {
        User sender = getUserByUsername(currentUsername);
        User receiver = getUserByUsername(targetUsername);

        Friendship friendship = friendshipRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy lời mời kết bạn tới @" + targetUsername));

        friendshipRepository.delete(friendship);
    }

    /**
     * Huy ket ban (Unfriend) giua currentUsername va targetUsername.
     */
    @Transactional
    public void unfriend(String currentUsername, String targetUsername) {
        User user1 = getUserByUsername(currentUsername);
        User user2 = getUserByUsername(targetUsername);

        Friendship friendship = friendshipRepository.findRelationBetween(user1.getId(), user2.getId())
                .orElseThrow(() -> new NotFoundException("Hai người hiện chưa phải là bạn bè"));

        friendshipRepository.delete(friendship);
    }

    /**
     * Kiem tra quan he giua currentUsername va targetUsername.
     */
    public FriendRelation getRelation(String currentUsername, String targetUsername) {
        if (currentUsername == null || targetUsername == null) {
            return FriendRelation.NONE;
        }
        if (currentUsername.equalsIgnoreCase(targetUsername)) {
            return FriendRelation.SELF;
        }

        Optional<User> optCurrent = userRepository.findByUsername(currentUsername);
        Optional<User> optTarget = userRepository.findByUsername(targetUsername);
        if (optCurrent.isEmpty() || optTarget.isEmpty()) {
            return FriendRelation.NONE;
        }

        Long curId = optCurrent.get().getId();
        Long tarId = optTarget.get().getId();

        Optional<Friendship> opt = friendshipRepository.findRelationBetween(curId, tarId);
        if (opt.isEmpty()) {
            return FriendRelation.NONE;
        }

        Friendship f = opt.get();
        if (f.getStatus() == FriendshipStatus.ACCEPTED) {
            return FriendRelation.FRIENDS;
        }
        if (f.getStatus() == FriendshipStatus.PENDING) {
            if (f.getSender().getId().equals(curId)) {
                return FriendRelation.PENDING_SENT;
            } else {
                return FriendRelation.PENDING_RECEIVED;
            }
        }

        return FriendRelation.NONE;
    }

    /**
     * Lay danh sach loi moi ket ban dang cho currentUsername chap nhan.
     */
    public List<FriendRequestView> getPendingRequestsReceived(String currentUsername) {
        User user = getUserByUsername(currentUsername);
        List<Friendship> requests = friendshipRepository.findByReceiverIdAndStatusWithSender(user.getId(), FriendshipStatus.PENDING);

        List<FriendRequestView> result = new ArrayList<>();
        for (Friendship f : requests) {
            User sender = f.getSender();
            UserCard card = userService.toUserCard(sender, false);
            String timeStr = f.getCreatedAt() != null ? f.getCreatedAt().format(DATE_FORMATTER) : "";
            result.add(new FriendRequestView(f.getId(), card, timeStr));
        }
        return result;
    }

    /**
     * Lay danh sach ban be chinh thuc (ACCEPTED) cua username.
     */
    public List<UserCard> getFriends(String username) {
        User user = getUserByUsername(username);
        List<Friendship> friendships = friendshipRepository.findAllAcceptedFriends(user.getId());

        List<UserCard> friends = new ArrayList<>();
        for (Friendship f : friendships) {
            User friend = f.getSender().getId().equals(user.getId()) ? f.getReceiver() : f.getSender();
            friends.add(userService.toUserCard(friend, false));
        }
        return friends;
    }

    /**
     * Dem tong so ban be chinh thuc.
     */
    public long countFriends(String username) {
        return userRepository.findByUsername(username)
                .map(u -> friendshipRepository.countAcceptedFriends(u.getId()))
                .orElse(0L);
    }

    /**
     * Dem so loi moi ket ban chua duyet.
     */
    public long countPendingRequests(String username) {
        return userRepository.findByUsername(username)
                .map(u -> friendshipRepository.countPendingReceived(u.getId()))
                .orElse(0L);
    }

    /**
     * Goi y ket ban: Nguoi dung chua ket ban va chua co loi moi ket ban voi currentUsername.
     */
    public List<UserCard> getSuggestedUsers(String currentUsername) {
        User current = getUserByUsername(currentUsername);
        List<User> allUsers = userRepository.findAll();
        List<UserCard> suggestions = new ArrayList<>();

        for (User u : allUsers) {
            if (u.getId().equals(current.getId())) {
                continue;
            }
            FriendRelation rel = getRelation(currentUsername, u.getUsername());
            if (rel == FriendRelation.NONE) {
                suggestions.add(userService.toUserCard(u, false));
            }
        }
        return suggestions;
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng @" + username));
    }
}
