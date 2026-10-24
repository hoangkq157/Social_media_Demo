package com.nhom2.social.exception;

/**
 * Ngoai le xay ra khi luu tru hoac xu ly file upload (file khong hop le, vuot dung luong...).
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
