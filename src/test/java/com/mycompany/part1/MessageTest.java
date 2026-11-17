package com.mycompany.part1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Message class (Part 2 test data).
 */
public class MessageTest {

    // Test data for message 1
    private static final String RECIPIENT_1 = "+27718693002";
    private static final String MESSAGE_1 = "Hi Mike, can you join us for dinner tonight";

    // Test data for message 2
    private static final String RECIPIENT_2 = "08575975889";
    private static final String MESSAGE_2 = "Hi Keegan, did you receive the payment?";

    @BeforeEach
    void setUp() {
        Message.reset();
    }

    // ========== MESSAGE LENGTH ==========

    @Test
    void testMessageLengthSuccess() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertTrue(message.checkMessageLength());
        assertEquals("Message ready to send.", message.getMessageLengthMessage());
    }

    @Test
    void testMessageLengthExactly250IsAllowed() {
        Message message = new Message(0, RECIPIENT_1, "a".repeat(250));
        assertTrue(message.checkMessageLength());
        assertEquals("Message ready to send.", message.getMessageLengthMessage());
    }

    @Test
    void testMessageLengthFailure() {
        Message message = new Message(0, RECIPIENT_1, "a".repeat(260));
        assertFalse(message.checkMessageLength());
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                message.getMessageLengthMessage());
    }

    // ========== RECIPIENT ==========

    @Test
    void testRecipientCorrectlyFormatted() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals(1, message.checkRecipientCell());
        assertEquals("Cell phone number successfully captured.", message.getRecipientCellMessage());
    }

    @Test
    void testRecipientIncorrectlyFormatted() {
        Message message = new Message(1, RECIPIENT_2, MESSAGE_2);
        assertEquals(0, message.checkRecipientCell());
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                message.getRecipientCellMessage());
    }

    // ========== MESSAGE HASH ==========

    @Test
    void testMessageHashCorrect() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("00:0:HITONIGHT", message.getMessageHash());
    }

    @Test
    void testMessageHashForAllTestDataInLoop() {
        String[] ids = {"0012345678", "1198765432"};
        String[] texts = {MESSAGE_1, MESSAGE_2};
        String[] expected = {"00:0:HITONIGHT", "11:1:HIPAYMENT"};

        for (int i = 0; i < texts.length; i++) {
            Message message = new Message(ids[i], i, RECIPIENT_1, texts[i]);
            assertEquals(expected[i], message.getMessageHash());
        }
    }

    @Test
    void testMessageHashUsesLoopCounterAsMessageNumber() {
        for (int i = 0; i < 5; i++) {
            Message message = new Message("4455667788", i, RECIPIENT_1, MESSAGE_1);
            assertEquals("44:" + i + ":HITONIGHT", message.getMessageHash());
        }
    }

    // ========== MESSAGE ID ==========

    @Test
    void testMessageIDCreated() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertTrue(message.checkMessageID());
        assertEquals("Message ID generated: " + message.getMessageID(),
                message.getMessageIDCreatedMessage());
    }

    @Test
    void testMessageIDCreatedCorrectlyInLoop() {
        for (int i = 0; i < 10; i++) {
            Message message = new Message(i, RECIPIENT_1, MESSAGE_1);
            String id = message.getMessageID();

            assertTrue(message.checkMessageID());
            assertEquals(10, id.length());
            assertTrue(id.matches("\\d{10}"));
            assertEquals("Message ID generated: " + id, message.getMessageIDCreatedMessage());
            // The hash starts with the first two digits of the ID
            assertEquals(id.substring(0, 2), message.getMessageHash().substring(0, 2));
        }
    }

    @Test
    void testMessageIDTooLongFails() {
        Message message = new Message("12345678901", 0, RECIPIENT_1, MESSAGE_1);
        assertFalse(message.checkMessageID());
    }

    // ========== SEND / DISREGARD / STORE ==========

    @Test
    void testMessageSentSend() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully sent.", message.sentMessage(1));
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void testMessageSentDisregard() {
        Message message = new Message(1, RECIPIENT_2, MESSAGE_2);
        assertEquals("Press 0 to delete the message.", message.sentMessage(2));
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    void testMessageSentStore() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully stored.", message.sentMessage(3));
        assertEquals(0, Message.returnTotalMessages());
    }

    @Test
    void testmessageSentInvalidOption() {
        Message message = new Message(0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Invalid option.", message.sentMessage(9));
    }

    // ========== TOTAL AND PRINT ==========

    @Test
    void testReturnTotalmessages() {
        new Message(0, RECIPIENT_1, MESSAGE_1).sentMessage(1);   // send
        new Message(1, RECIPIENT_2, MESSAGE_2).sentMessage(2);   // disregard
        new Message(2, RECIPIENT_1, MESSAGE_1).sentMessage(1);   // send
        assertEquals(2, Message.returnTotalMessages());
    }

    @Test
    void testPrintMessageOrder() {
        Message message = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Message ID: 0012345678"
                + "\nMessage Hash: 00:0:HITONIGHT"
                + "\nRecipient: +27718693002"
                + "\nMessage: Hi Mike, can you join us for dinner tonight",
                message.printMessage());
    }
}