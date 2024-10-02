package com.gap.readliness.util;

import com.gap.readliness.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GenerateUtil {

    @Autowired
    private ItemRepository itemRepository;

    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 4;

    private Set<String> existingOrderCodes = new HashSet<>();

    public String generateUniqueCode(Date currentDate) {
        String orderCode;

        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyy");
        String formattedDate = dateFormat.format(currentDate);

        do {
            orderCode = formattedDate + generateOrderCode();
        } while (existingOrderCodes.contains(orderCode));

        existingOrderCodes.add(orderCode); // Store the unique order code
        return orderCode;
    }

    private String generateOrderCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder orderCodeBuilder = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(ALPHANUMERIC_STRING.length());
            orderCodeBuilder.append(ALPHANUMERIC_STRING.charAt(index));
        }

        return orderCodeBuilder.toString();
    }

    public String generateItemCode() {
        String result = null;
        Optional<String> itemCodes = itemRepository.findMaxItemCode();

        if (itemCodes.isPresent()) {
            String maxItemCode = itemCodes.get();

            Pattern pattern = Pattern.compile("([A-Za-z]+)(\\d+)");
            Matcher matcher = pattern.matcher(maxItemCode);

            if (matcher.find()) {
                String prefix = matcher.group(1);
                int numericPart = Integer.parseInt(matcher.group(2));
                numericPart++;

                result = prefix + numericPart;
            }
        }

        return result;
    }
}
