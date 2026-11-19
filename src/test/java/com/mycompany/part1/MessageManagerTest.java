package com.mycompany.part1;

import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MessageManager class (Part 3 test data).
 */
public class MessageManagerTest {

    private MessageManager manager;
    private Message message2;

    @BeforeEach
    void setUp() {
        // Test data from the brief (message 4's ID is the developer number given in the brief)
        Message message1 = new Message("1111111111", 0, "+27834557896", "Did you get the cake?");
        message2 = new Message("2222222222", 1, "+27838884567", "Where are you? You are late! I have asked you to be on time.");
        Message message3 = new Message("3333333333", 2, "+27834484567", "Yohoooo, I am at your gate.");
        Message message4 = new Message("0838884567", 3, "0838884567", "It is dinner time!");
        Message message5 = new Message("5555555555", 4, "+27838884567", "Ok, I am leaving without you.");

        manager = new MessageManager();
        manager.addMessage(message1, MessageManager.SENT);
        manager.addMessage(message2, MessageManager.STORED);
        manager.addMessage(message3, MessageManager.DISREGARDED);
        manager.addMessage(message4, MessageManager.SENT);
        manager.addMessage(message5, MessageManager.STORED);
    }

    @Test
    void testSentMessagesArrayCorrectlyPopulated() {
        assertArrayEquals(new String[]{"Did you get the cake?", "It is dinner time!"},
                manager.getSentMessages());
    }

    @Test
    void testDisregardedAndStoredArraysPopulated() {
        assertArrayEquals(new String[]{"Yohoooo, I am at your gate."}, manager.getDisregardedMessages());
        assertArrayEquals(new String[]{"Where are you? You are late! I have asked you to be on time.",
            "Ok, I am leaving without you."}, manager.getStoredMessages());
    }

    @Test
    void testMessageIDAndHashArraysPopulated() {
        assertEquals(5, manager.getMessageIDs().length);
        assertEquals("1111111111", manager.getMessageIDs()[0]);
        assertEquals(5, manager.getMessageHashes().length);
        assertEquals("11:0:DIDCAKE", manager.getMessageHashes()[0]);
    }

    @Test
    void testDisplayLongestMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                manager.getLongestMessage());
    }

    @Test
    void testSearchForMessageID() {
        assertEquals("It is dinner time!", manager.searchByMessageID("0838884567"));
    }

    @Test
    void testSearchForMessageIDNotFound() {
        assertEquals("Message ID not found.", manager.searchByMessageID("9999999999"));
    }

    @Test
    void testSearchAllMessagesForRecipient() {
        assertArrayEquals(new String[]{"Where are you? You are late! I have asked you to be on time.",
            "Ok, I am leaving without you."}, manager.searchByRecipient("+27838884567"));
    }

    @Test
    void testDeleteMessageUsingMessageHash() {
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                manager.deleteByMessageHash(message2.getMessageHash()));
        assertEquals(4, manager.getCount());
        assertEquals("Message hash not found.", manager.deleteByMessageHash(message2.getMessageHash()));
    }

    @Test
    void testDisplayReport() {
        String report = manager.displayReport();
        assertTrue(report.contains("Message Hash: 11:0:DIDCAKE"));
        assertTrue(report.contains("Recipient: +27834557896"));
        assertTrue(report.contains("Message: Did you get the cake?"));
        assertTrue(report.contains("Message: It is dinner time!"));
        assertFalse(report.contains("Yohoooo"));   // disregarded messages are not in the report
    }

    @Test
    void testDisplaySenderAndRecipients() {
        assertEquals("Sender: Kyle Smith | Recipient: +27838884567\n"
                + "Sender: Kyle Smith | Recipient: +27838884567",
                manager.displaySenderAndRecipients("Kyle Smith"));
    }

    @Test
    void testStoredMessagesSavedAndReadFromJson(@TempDir Path folder) {
        String file = folder.resolve("messages.json").toString();
        assertTrue(manager.saveStoredMessagesToJson(file));

        MessageManager loaded = new MessageManager();
        assertEquals(2, loaded.loadStoredMessagesFromJson(file));
        assertArrayEquals(manager.getStoredMessages(), loaded.getStoredMessages());
    }
}