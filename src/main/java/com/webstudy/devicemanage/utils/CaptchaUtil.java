package com.webstudy.devicemanage.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptchaUtil {
    private static final int CAPTCHA_WIDTH = 100;
    private static final int CAPTCHA_HEIGHT = 40;
    private static final int CAPTCHA_LENGTH = 4;

    private static final String CAPTCHA_FONT_FAMILY = "Arial";
    private static final int CAPTCHA_FONT_SIZE = 18;

    private static final Random random = new Random();

    public static String generateCaptcha() {
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captcha.append(randomChar());
        }
        return captcha.toString();
    }

    public static BufferedImage generateCaptchaImage(String captcha) {
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();

        // 绘制背景
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);

        // 绘制验证码
        g2.setFont(new Font(CAPTCHA_FONT_FAMILY, Font.PLAIN, CAPTCHA_FONT_SIZE));
        for (int i = 0; i < captcha.length(); i++) {
            g2.setColor(randomColor());
            g2.drawString(String.valueOf(captcha.charAt(i)), 20 * i + 10, 25);
        }

        // 绘制干扰线
        for (int i = 0; i < 5; i++) {
            g2.setColor(randomColor());
            g2.drawLine(random.nextInt(CAPTCHA_WIDTH), random.nextInt(CAPTCHA_HEIGHT),
                    random.nextInt(CAPTCHA_WIDTH), random.nextInt(CAPTCHA_HEIGHT));
        }

        g2.dispose();

        return image;
    }

    private static char randomChar() {
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return chars.charAt(random.nextInt(chars.length()));
    }

    private static Color randomColor() {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}

