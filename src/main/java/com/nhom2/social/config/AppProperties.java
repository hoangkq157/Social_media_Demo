package com.nhom2.social.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cau hinh tuy bien ung dung doc tu application.properties qua @ConfigurationProperties.
 * Thao tac an toan kieu, khong can hard-code cac thong so upload va phan trang.
 */
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Upload upload = new Upload();
    private final Feed feed = new Feed();

    public Upload getUpload() {
        return upload;
    }

    public Feed getFeed() {
        return feed;
    }

    public static class Upload {
        /** Thu muc luu file nguoi dung tai len (mac dinh: ./uploads) */
        private String dir = "./uploads";
        /** Dung luong anh toi da tinh bang MB */
        private int maxImageMb = 5;
        /** Dung luong video toi da tinh bang MB */
        private int maxVideoMb = 50;

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }

        public int getMaxImageMb() {
            return maxImageMb;
        }

        public void setMaxImageMb(int maxImageMb) {
            this.maxImageMb = maxImageMb;
        }

        public int getMaxVideoMb() {
            return maxVideoMb;
        }

        public void setMaxVideoMb(int maxVideoMb) {
            this.maxVideoMb = maxVideoMb;
        }
    }

    public static class Feed {
        /** So luong bai viet tren moi trang bang tin */
        private int pageSize = 10;

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }
}
