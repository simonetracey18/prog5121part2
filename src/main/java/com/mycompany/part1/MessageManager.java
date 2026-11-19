package com.mycompany.part1;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Part 3 - keeps every message in parallel arrays and lets the user
 * search, delete and report on them. Index i in every array belongs
 * to the same message.
 *
 * @author Student
 */
public class MessageManager {

    public static final String SENT = "Sent";
    public static final String STORED = "Stored";
    public static final String DISREGARDED = "Disregard";

    private static final int MAX_MESSAGES = 100;

    // ===== Parallel arrays =====
    private final String[] messageIDs = new String[MAX_MESSAGES];
    private final String[] messageHashes = new String[MAX_MESSAGES];
    private final String[] recipients = new String[MAX_MESSAGES];
    private final String[] messages = new String[MAX_MESSAGES];
    private final String[] flags = new String[MAX_MESSAGES];
    private int count = 0;

    // ===== Populate the arrays =====
    public boolean addMessage(String messageID, String messageHash, String recipient,
                              String message, String flag) {
        if (count >= MAX_MESSAGES) {
            return false;
        }
        messageIDs[count] = messageID;
        messageHashes[count] = messageHash;
        recipients[count] = recipient;
        messages[count] = message;
        flags[count] = flag;
        count++;
        return true;
    }

    public boolean addMessage(Message message, String flag) {
        return addMessage(message.getMessageID(), message.getMessageHash(),
                message.getRecipient(), message.getMessageText(), flag);
    }

    public int getCount() {
        return count;
    }

    // ===== The arrays from the brief =====
    public String[] getSentMessages() {
        return messagesWithFlag(SENT);
    }

    public String[] getDisregardedMessages() {
        return messagesWithFlag(DISREGARDED);
    }

    public String[] getStoredMessages() {
        return messagesWithFlag(STORED);
    }

    public String[] getMessageHashes() {
        return copyOf(messageHashes);
    }

    public String[] getMessageIDs() {
        return copyOf(messageIDs);
    }

    // Builds an array of the message texts that have the given flag
    private String[] messagesWithFlag(String flag) {
        int matches = 0;
        for (int i = 0; i < count; i++) {
            if (flags[i].equals(flag)) {
                matches++;
            }
        }
        String[] result = new String[matches];
        int position = 0;
        for (int i = 0; i < count; i++) {
            if (flags[i].equals(flag)) {
                result[position] = messages[i];
                position++;
            }
        }
        return result;
    }

    private String[] copyOf(String[] source) {
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = source[i];
        }
        return result;
    }
// ===== Recently sent messages (main menu option 2) =====
    public String displaySentMessages() {
        String output = "";
        for (int i = 0; i < count; i++) {
            if (flags[i].equals(SENT)) {
                output += "Message ID: " + messageIDs[i]
                        + "\nRecipient: " + recipients[i]
                        + "\nMessage: " + messages[i]
                        + "\n------------------------------\n";
            }
        }
        return output.isEmpty() ? "No messages have been sent yet." : output.trim();
    }
    // ===== a) Sender and recipient of all stored messages =====
    public String displaySenderAndRecipients(String sender) {
        String output = "";
        for (int i = 0; i < count; i++) {
            if (flags[i].equals(STORED)) {
                output += "Sender: " + sender + " | Recipient: " + recipients[i] + "\n";
            }
        }
        return output.isEmpty() ? "No stored messages." : output.trim();
    }

    // ===== b) Longest stored message =====
    public String getLongestMessage() {
        String longest = "";
        for (int i = 0; i < count; i++) {
            if (flags[i].equals(STORED) && messages[i].length() > longest.length()) {
                longest = messages[i];
            }
        }
        return longest.isEmpty() ? "No stored messages." : longest;
    }

    // ===== c) Search by message ID =====
    public String searchByMessageID(String messageID) {
        int index = indexOf(messageIDs, messageID);
        return index == -1 ? "Message ID not found." : messages[index];
    }

    public String displayMessageByID(String messageID) {
        int index = indexOf(messageIDs, messageID);
        if (index == -1) {
            return "Message ID not found.";
        }
        return "Recipient: " + recipients[index] + "\nMessage: " + messages[index];
    }
    // ===== d) Search by recipient (sent or stored messages) =====
    public String[] searchByRecipient(String recipient) {
        int matches = 0;
        for (int i = 0; i < count; i++) {
            if (recipients[i].equals(recipient) && !flags[i].equals(DISREGARDED)) {
                matches++;
            }
        }
        String[] result = new String[matches];
        int position = 0;
        for (int i = 0; i < count; i++) {
            if (recipients[i].equals(recipient) && !flags[i].equals(DISREGARDED)) {
                result[position] = messages[i];
                position++;
            }
        }
        return result;
    }

    // ===== e) Delete using the message hash =====
    public String deleteByMessageHash(String messageHash) {
        int index = indexOf(messageHashes, messageHash);
        if (index == -1) {
            return "Message hash not found.";
        }
        String deletedMessage = messages[index];

        // Shift every later message one place to the left in all the parallel arrays
        for (int i = index; i < count - 1; i++) {
            messageIDs[i] = messageIDs[i + 1];
            messageHashes[i] = messageHashes[i + 1];
            recipients[i] = recipients[i + 1];
            messages[i] = messages[i + 1];
            flags[i] = flags[i + 1];
        }
        count--;
        messageIDs[count] = null;
        messageHashes[count] = null;
        recipients[count] = null;
        messages[count] = null;
        flags[count] = null;

        return "Message: \"" + deletedMessage + "\" successfully deleted.";
    }

    // ===== f) Report with the full details of every sent and stored message =====
    public String displayReport() {
        String report = "";
        for (int i = 0; i < count; i++) {
            if (!flags[i].equals(DISREGARDED)) {
                report += "Message ID: " + messageIDs[i]
                        + "\nMessage Hash: " + messageHashes[i]
                        + "\nRecipient: " + recipients[i]
                        + "\nMessage: " + messages[i]
                        + "\nStatus: " + flags[i]
                        + "\n------------------------------\n";
            }
        }
        return report.isEmpty() ? "No messages to report." : report.trim();
    }

    private int indexOf(String[] array, String value) {
        for (int i = 0; i < count; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    // ===== JSON file =====
    // JSON format reference: ECMA International (2017) The JSON Data Interchange Syntax,
    // ECMA-404. Available at: https://www.json.org/json-en.html
    // File writing reference: Oracle (2024) FileWriter (Java SE 17 API). Available at:
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/FileWriter.html
    public boolean saveStoredMessagesToJson(String fileName) {
        String json = "[\n";
        boolean first = true;
        for (int i = 0; i < count; i++) {
            if (flags[i].equals(STORED)) {
                if (!first) {
                    json += ",\n";
                }
                json += "  {\"messageID\": \"" + escape(messageIDs[i]) + "\", "
                        + "\"messageHash\": \"" + escape(messageHashes[i]) + "\", "
                        + "\"recipient\": \"" + escape(recipients[i]) + "\", "
                        + "\"message\": \"" + escape(messages[i]) + "\"}";
                first = false;
            }
        }
        json += "\n]";

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            return true;
        } catch (IOException e) {
            System.out.println("Could not save messages: " + e.getMessage());
            return false;
        }
    }

    // Reads the stored messages from the JSON file into the arrays.
    // File reading reference: Oracle (2024) Files.readString (Java SE 17 API). Available at:
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/nio/file/Files.html
    // Regular expression reference: Oracle (2024) Pattern (Java SE 17 API). Available at:
    // https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/regex/Pattern.html
    public int loadStoredMessagesFromJson(String fileName) {
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            return 0;
        }
        String json;
        try {
            json = Files.readString(path);
        } catch (IOException e) {
            System.out.println("Could not read messages: " + e.getMessage());
            return 0;
        }

        Pattern objectPattern = Pattern.compile("\\{([^{}]*)\\}");
        Matcher objectMatcher = objectPattern.matcher(json);
        int loaded = 0;
        while (objectMatcher.find()) {
            String object = objectMatcher.group(1);
            String id = readField(object, "messageID");
            String hash = readField(object, "messageHash");
            String recipient = readField(object, "recipient");
            String message = readField(object, "message");
            if (id != null && hash != null && recipient != null && message != null
                    && addMessage(id, hash, recipient, message, STORED)) {
                loaded++;
            }
        }
        return loaded;
    }

    private String readField(String object, String key) {
        Pattern fieldPattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");
        Matcher fieldMatcher = fieldPattern.matcher(object);
        return fieldMatcher.find() ? unescape(fieldMatcher.group(1)) : null;
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unescape(String text) {
        return text.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    int loadStoredMessageFromJson(String JSON_FILE) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
