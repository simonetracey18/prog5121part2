package com.mycompany.part1;

import java.util.Scanner;

/**
 * QuickChat: Part 1 registration and login, Part 2 sending messages,
 * Part 3 stored messages.
 *
 * @author Student
 */
public class Main {

    private static final String JSON_FILE = "messages.json";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Login loginSystem = new Login();

        System.out.println("=== Registration ===");

        // ===== Username with loop until valid =====
        String username;
        while (true) {
            System.out.print("Enter username: ");
            username = input.nextLine();
            System.out.println(loginSystem.getUsernameCaptureMessage(username));
            if (loginSystem.checkUserName(username)) {
                break;
            }
            System.out.println("Please try again.\n");
        }

        // ===== Password =====
        String password;
        while (true) {
            System.out.print("Enter password: ");
            password = input.nextLine();
            System.out.println(loginSystem.getPasswordCaptureMessage(password));
            if (loginSystem.checkPasswordComplexity(password)) {
                break;
            }
            System.out.println("Please try again.\n");
        }

        // ===== Cell Phone =====
        String cellPhone;
        while (true) {
            System.out.print("Enter South African cell phone number (with +27): ");
            cellPhone = input.nextLine();
            System.out.println(loginSystem.getCellPhoneCaptureMessage(cellPhone));
            if (loginSystem.checkCellPhoneNumber(cellPhone)) {
                break;
            }
            System.out.println("Please try again.\n");
        }

        // ===== First & Last name =====
        System.out.print("Enter first name: ");
        String firstName = input.nextLine();
        System.out.print("Enter last name: ");
        String lastName = input.nextLine();

        String registrationResult = loginSystem.registerUser(username, password, cellPhone, firstName, lastName);
        System.out.println("\n" + registrationResult);

        // ===== Login (repeat until correct) =====
        System.out.println("\n=== Login ===");
        while (true) {
            System.out.print("Enter username: ");
            String loginUsername = input.nextLine();
            System.out.print("Enter password: ");
            String loginPassword = input.nextLine();

            System.out.println(loginSystem.returnLoginStatus(loginUsername, loginPassword));
            if (loginSystem.loginUser(loginUsername, loginPassword)) {
                break;          // only logged in users may send messages
            }
        }

        // ===== QuickChat =====
        MessageManager manager = new MessageManager();
        int loaded = manager.loadStoredMessagesFromJson(JSON_FILE);
        String sender = firstName + " " + lastName;

        System.out.println("\nWelcome to QuickChat.");
        if (loaded > 0) {
            System.out.println(loaded + " stored message(s) loaded from " + JSON_FILE + ".");
        }
        int maxMessages = readNumber(input, "How many messages would you like to send? ");
        boolean messagesEntered = false;

        int option = 0;
        while (option != 3) {
            System.out.println("\nPlease choose an option:");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            option = readNumber(input, "Option: ");

            switch (option) {
                case 1:
                    if (messagesEntered) {
                        System.out.println("You have already entered all " + maxMessages + " messages.");
                        break;
                    }
                    // For loop: the user may only enter the set number of messages.
                    // The loop counter i is used as the message number.
                    for (int i = 0; i < maxMessages; i++) {
                        System.out.println("\n--- Message " + (i + 1) + " of " + maxMessages + " ---");
                        enterMessage(input, i, manager);
                    }
                    messagesEntered = true;
                    System.out.println("\nTotal number of messages sent: " + Message.returnTotalMessages());
                    break;
                case 2:
                  System.out.println(manager.displaySentMessages());
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    break;
                case 4:
                    storedMessagesMenu(input, manager, sender);
                    break;
                default:
                    System.out.println("Invalid option, please choose 1, 2, 3 or 4.");
            }
        }

        input.close();
    }

    private static void enterMessage(Scanner input, int messageNumber, MessageManager manager) {
        // ===== Recipient =====
        String recipient;
        while (true) {
            System.out.print("Enter recipient cell number (with +27): ");
            recipient = input.nextLine();
            Message check = new Message(messageNumber, recipient, "x");
            System.out.println(check.getRecipientCellMessage());
            if (check.checkRecipientCell() == 1) {
                break;
            }
        }
        // ===== Message text =====
        String text;
        while (true) {
            System.out.print("Enter your message: ");
            text = input.nextLine();
            if (text.trim().isEmpty()) {
                System.out.println("Message cannot be empty.");
            } else if (text.length() > Message.MAX_MESSAGE_LENGTH) {
                System.out.println("Please enter a message of less than 250 characters.");
            } else {
                break;
            }
        }

        Message message = new Message(messageNumber, recipient, text);
        System.out.println(message.getMessageIDCreatedMessage());

        // ===== Send / Disregard / Store =====
        int choice = 0;
        while (choice < 1 || choice > 3) {
            System.out.println("What would you like to do with this message?");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            choice = readNumber(input, "Choice: ");
        }

        System.out.println(message.sentMessage(choice));

        if (choice == 1) {
            manager.addMessage(message, MessageManager.SENT);
            System.out.println("\n" + message.printMessage());
        } else if (choice == 2) {
            manager.addMessage(message, MessageManager.DISREGARDED);
            // Wait for the user to confirm deleting the message
            while (!input.nextLine().trim().equals("0")) {
                System.out.println("Press 0 to delete the message.");
            }
            System.out.println("Message deleted.");
        } else {
            manager.addMessage(message, MessageManager.STORED);
            manager.saveStoredMessagesToJson(JSON_FILE);
        }
    }

    // ===== Part 3: Stored Messages menu =====
    private static void storedMessagesMenu(Scanner input, MessageManager manager, String sender) {
        int choice = 0;
        while (choice != 7) {
            System.out.println("\n=== Stored Messages ===");
            System.out.println("1) Display the sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages to a recipient");
            System.out.println("5) Delete a message using the message hash");
            System.out.println("6) Display a report of all messages");
            System.out.println("7) Back to main menu");
            choice = readNumber(input, "Option: ");

            switch (choice) {
                case 1:
                    System.out.println(manager.displaySenderAndRecipients(sender));
                    break;
                case 2:
                    System.out.println("Longest message: " + manager.getLongestMessage());
                    break;
                case 3:
                    System.out.print("Enter the message ID: ");
                    System.out.println(manager.displayMessageByID(input.nextLine().trim()));
                    break;
                case 4:
                    System.out.print("Enter the recipient's cell number: ");
                    String[] found = manager.searchByRecipient(input.nextLine().trim());
                    if (found.length == 0) {
                        System.out.println("No messages found for that recipient.");
                    }
                    for (String message : found) {
                        System.out.println("\"" + message + "\"");
                    }
                    break;
                case 5:
                    System.out.print("Enter the message hash: ");
                    System.out.println(manager.deleteByMessageHash(input.nextLine().trim().toUpperCase()));
                    manager.saveStoredMessagesToJson(JSON_FILE);
                    break;
                case 6:
                    System.out.println(manager.displayReport());
                    break;
                case 7:
                    break;
                default:
                    System.out.println("Invalid option, please choose 1 to 7.");
            }
        }
    }

    // Keeps asking until the user types a whole number
    private static int readNumber(Scanner input, String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = input.nextLine().trim();
            try {
                return Integer.parseInt(answer);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}