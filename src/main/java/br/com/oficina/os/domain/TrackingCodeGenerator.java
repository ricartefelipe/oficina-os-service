package br.com.oficina.os.domain;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class TrackingCodeGenerator {

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyMMdd");

    private TrackingCodeGenerator() {}

    public static String generate() {
        String date = LocalDate.now().format(FMT);
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return date + sb;
    }
}
