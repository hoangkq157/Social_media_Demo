package com.nhom2.social;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class SeedGeneratorTest {

    @Test
    void generateSeedImages() throws IOException {
        createBanner("seed1.jpg", "Spring Boot 4.x Architecture", "DispatcherServlet -> Controller -> Service -> Repository", new Color(24, 119, 242), new Color(13, 71, 161));
        createBanner("seed2.jpg", "Thymeleaf Server-Side Rendering", "Dynamic templates & reusable layout fragments", new Color(16, 185, 129), new Color(5, 150, 105));
        createBanner("seed3.jpg", "Spring Data JPA & Hibernate", "Efficient data access, pagination & batch fetching", new Color(139, 92, 246), new Color(109, 40, 217));
        createBanner("seed4.jpg", "Spring Security 7 + BCrypt", "Form login, CSRF protection & role-based security", new Color(245, 158, 11), new Color(217, 119, 6));
    }

    private void createBanner(String filename, String title, String subtitle, Color color1, Color color2) throws IOException {
        int width = 800;
        int height = 450;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background Gradient
        GradientPaint gradient = new GradientPaint(0, 0, color1, width, height, color2);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Decorative circles
        g2d.setColor(new Color(255, 255, 255, 25));
        g2d.fillOval(-50, -50, 300, 300);
        g2d.fillOval(width - 250, height - 250, 350, 350);

        // Brand badge
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.fillRoundRect(50, 50, 150, 40, 20, 20);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2d.drawString("SpringBook", 72, 76);

        // Title
        g2d.setFont(new Font("SansSerif", Font.BOLD, 36));
        FontMetrics fmTitle = g2d.getFontMetrics();
        g2d.drawString(title, 50, 220);

        // Subtitle
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 20));
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.drawString(subtitle, 50, 270);

        // Bottom footer
        g2d.setFont(new Font("SansSerif", Font.ITALIC, 16));
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.drawString("Nhóm 2 - PTPM Hướng Đối Tượng - Demo", 50, 400);

        g2d.dispose();

        File outputDir = new File("src/main/resources/static/seed");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        ImageIO.write(image, "jpg", new File(outputDir, filename));
    }
}
