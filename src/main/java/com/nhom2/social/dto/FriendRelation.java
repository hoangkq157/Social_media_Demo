package com.nhom2.social.dto;

/**
 * Quan he ban be giua nguoi dung hien tai va nguoi dung muc tieu.
 */
public enum FriendRelation {
    SELF,             // Chinh la ban than
    FRIENDS,          // Da la ban be (ACCEPTED)
    PENDING_SENT,     // Nguoi dung hien tai da gui loi moi ket ban
    PENDING_RECEIVED, // Nguoi dung hien tai nhan duoc loi moi ket ban
    NONE              // Chua co quan he ket ban nao
}
