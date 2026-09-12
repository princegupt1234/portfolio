package com.example.portfolio;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AssetGeneratorTest {

    @Test
    void generateBrandAssets() throws IOException {
        File imagesDir = new File("src/main/resources/static/images");
        if (!imagesDir.exists()) imagesDir.mkdirs();

        File iconsDir = new File("src/main/resources/static/icons");
        if (!iconsDir.exists()) iconsDir.mkdirs();

        generateOgPreview(new File(imagesDir, "og-preview.png"));
        generateAppIcon(new File(iconsDir, "icon-192.png"), 192);
        generateAppIcon(new File(iconsDir, "icon-512.png"), 512);
    }

    private void generateOgPreview(File target) throws IOException {
        int width = 1200;
        int height = 630;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 1. Base dark background
        g2.setColor(new Color(10, 15, 29)); // #0a0f1d
        g2.fillRect(0, 0, width, height);

        // 2. Aurora glow blobs
        g2.setPaint(new RadialGradientPaint(200, 150, 450,
                new float[]{0f, 0.5f, 1f},
                new Color[]{new Color(59, 130, 246, 70), new Color(139, 92, 246, 30), new Color(10, 15, 29, 0)}));
        g2.fillOval(-150, -150, 800, 700);

        g2.setPaint(new RadialGradientPaint(1000, 480, 400,
                new float[]{0f, 0.6f, 1f},
                new Color[]{new Color(6, 182, 212, 60), new Color(59, 130, 246, 25), new Color(10, 15, 29, 0)}));
        g2.fillOval(750, 200, 700, 600);

        // 3. Grid line overlay
        g2.setColor(new Color(255, 255, 255, 6));
        for (int x = 0; x < width; x += 40) g2.drawLine(x, 0, x, height);
        for (int y = 0; y < height; y += 40) g2.drawLine(0, y, width, y);

        // 4. Main Glass Container Card
        int cardX = 60, cardY = 50, cardW = 1080, cardH = 530;
        g2.setColor(new Color(17, 24, 39, 210)); // #111827 with alpha
        g2.fill(new RoundRectangle2D.Float(cardX, cardY, cardW, cardH, 28, 28));

        // Card Border with gradient
        g2.setPaint(new GradientPaint(cardX, cardY, new Color(59, 130, 246, 120),
                cardX + cardW, cardY + cardH, new Color(139, 92, 246, 70)));
        g2.setStroke(new BasicStroke(2f));
        g2.draw(new RoundRectangle2D.Float(cardX, cardY, cardW, cardH, 28, 28));

        // 5. Card Content
        // Kicker / Eyebrow
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.setColor(new Color(56, 189, 248)); // #38bdf8 cyan
        g2.drawString("// SOFTWARE ENGINEER & FULL STACK DEVELOPER", cardX + 50, cardY + 70);

        // Name
        g2.setFont(new Font("SansSerif", Font.BOLD, 68));
        g2.setColor(Color.WHITE);
        g2.drawString("Prince Gupt", cardX + 48, cardY + 150);

        // Dot accent
        g2.setColor(new Color(59, 130, 246));
        g2.drawString(".", cardX + 48 + g2.getFontMetrics().stringWidth("Prince Gupt"), cardY + 150);

        // Role & Subtitle
        g2.setFont(new Font("SansSerif", Font.PLAIN, 26));
        g2.setColor(new Color(203, 213, 225)); // #cbd5e1
        g2.drawString("Java 17+  ·  Spring Boot 3  ·  MySQL  ·  REST APIs  ·  React", cardX + 50, cardY + 205);

        // Divider
        g2.setColor(new Color(255, 255, 255, 25));
        g2.drawLine(cardX + 50, cardY + 240, cardX + cardW - 50, cardY + 240);

        // Feature highlights (Pill Badges)
        drawPill(g2, cardX + 50, cardY + 270, "⚡ Production-Ready Full Stack", new Color(59, 130, 246));
        drawPill(g2, cardX + 370, cardY + 270, "☕ Spring Data JPA & Security", new Color(16, 185, 129));
        drawPill(g2, cardX + 680, cardY + 270, "💻 LeetCode & Problem Solving", new Color(245, 158, 11));

        drawPill(g2, cardX + 50, cardY + 340, "🎓 Final-Year B.Tech CSE (BIET Lucknow)", new Color(139, 92, 246));
        drawPill(g2, cardX + 470, cardY + 340, "🟢 Open for SDE-1 / Backend Roles", new Color(16, 185, 129));

        // Bottom Footer Bar inside card
        g2.setColor(new Color(15, 23, 42, 180));
        g2.fill(new RoundRectangle2D.Float(cardX + 30, cardY + cardH - 85, cardW - 60, 60, 16, 16));
        g2.setColor(new Color(255, 255, 255, 20));
        g2.draw(new RoundRectangle2D.Float(cardX + 30, cardY + cardH - 85, cardW - 60, 60, 16, 16));

        // Live dot
        g2.setColor(new Color(16, 185, 129));
        g2.fillOval(cardX + 55, cardY + cardH - 58, 12, 12);

        g2.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2.setColor(new Color(241, 245, 249));
        g2.drawString("portfolio-gx88.onrender.com", cardX + 80, cardY + cardH - 48);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g2.setColor(new Color(148, 163, 184));
        g2.drawString("Interactive Portfolio  ·  Live Demos  ·  CLI Terminal  ·  AI Assistant", cardX + 460, cardY + cardH - 48);

        g2.dispose();
        ImageIO.write(image, "png", target);
    }

    private void drawPill(Graphics2D g2, int x, int y, String text, Color accent) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 17));
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(text);
        int pillW = textW + 36;
        int pillH = 42;

        g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25));
        g2.fill(new RoundRectangle2D.Float(x, y, pillW, pillH, 20, 20));

        g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 90));
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(new RoundRectangle2D.Float(x, y, pillW, pillH, 20, 20));

        g2.setColor(new Color(241, 245, 249));
        g2.drawString(text, x + 18, y + 27);
    }

    private void generateAppIcon(File target, int size) throws IOException {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        // Background rounded square
        int corner = (int) (size * 0.22);
        g2.setColor(new Color(11, 15, 25));
        g2.fill(new RoundRectangle2D.Float(0, 0, size, size, corner, corner));

        // Border gradient
        g2.setPaint(new GradientPaint(0, 0, new Color(59, 130, 246), size, size, new Color(139, 92, 246)));
        g2.setStroke(new BasicStroke(size * 0.035f));
        int pad = (int) (size * 0.02);
        g2.draw(new RoundRectangle2D.Float(pad, pad, size - pad * 2, size - pad * 2, corner, corner));

        // Monogram "P."
        g2.setPaint(new GradientPaint(0, 0, new Color(96, 165, 250), size, size, new Color(168, 85, 247)));
        int fontSize = (int) (size * 0.52);
        g2.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        FontMetrics fm = g2.getFontMetrics();
        String text = "P.";
        int textX = (size - fm.stringWidth(text)) / 2;
        int textY = (size - fm.getHeight()) / 2 + fm.getAscent() + (int) (size * 0.02);
        g2.drawString(text, textX, textY);

        g2.dispose();
        ImageIO.write(image, "png", target);
    }
}
