import java.sql.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class visitorLogIn {

    static Repository repo = Repository.getInstance();
    public static void login() {
        Scanner sc = new Scanner(System.in);

        int loginChoice;

        System.out.println("== Returning Customer Login! ==\n");
        System.out.println("How can i help you?\n" +
                "[1] Login/Enter ID:\n" +
                "[2] I forgot my Customer ID:\n" +
                "[0] Exit\n");

        System.out.print("Enter your choice: ");
        loginChoice = sc.nextInt();
        sc.nextLine();

        do {
            switch (loginChoice) {
                case 1:
                    System.out.print("Please enter your customer ID: ");
                    int loginID = sc.nextInt();
                    sc.nextLine();

                    if (loginID >= 10000 && loginID <= 99999) {
                        int result = repo.findCustomerByID(loginID);

                        if (result != -1) {
                            customerPortal.portalMenu(result);
                        } else {
                            System.out.println("Customer ID not found. Please try again.");
                        }

                    } else {
                        int forgotChoice;
                        System.out.println("Your Customer ID has not been found.");
                        System.out.println("Please seek help to our \"I forgot my Customer ID\" menu.\n");
                        System.out.println("[1] I forgot my Customer ID");
                        System.out.println("[0] Exit");

                        System.out.print("Enter your choice: ");
                        forgotChoice = sc.nextInt();
                        sc.nextLine();

                        switch (forgotChoice) {
                            case 1:
                                forgotCustomerID();
                                break;

                            case 0:
                                System.exit(0);

                            default:
                                System.out.println("Invalid choice, please try again.");
                                System.out.print("Enter your choice: ");
                                forgotChoice = sc.nextInt();
                                sc.nextLine();
                                break;
                        }
                    }
                    loginChoice = 0;
                    break;

                case 2:
                    forgotCustomerID();
                    break;

                case 0:
                    System.out.println("System exiting....\nThank you!!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice, please try again.");
                    System.out.print("Enter your choice: ");
                    loginChoice = sc.nextInt();
                    sc.nextLine();
                    break;
            }

        } while (loginChoice != 0);

    }

    public static void forgotCustomerID() {
        Scanner sc = new Scanner(System.in);

        System.out.println("== Customer ID Recovery ==\n" +
                "Please provide the following information to retrieve your Customer ID.");

        System.out.print("Enter your Full Name: ");
        String forgotFullName = sc.nextLine();

        System.out.print("Enter your registered Contact Number: ");
        String forgotContactNumber = sc.nextLine();

        int foundID = repo.findCustomerByDetails(forgotFullName, forgotContactNumber);

        if (foundID != -1) {
            System.out.println("Checking customer records....");
            System.out.println("Customer Record found!");
            System.out.println("\nYour customer ID is: " + foundID);
            System.out.println("Please keep your Customer ID for future transactions.");
            System.out.println("Redirecting to Log In menu....\n");
            login();
        } else {
            System.out.println("Your Customer Record not found.");
            System.out.println("Please check your information and try again.");
            System.out.println("[1] Try again");
            System.out.println("[2] Register as a new customer");
            System.out.println("[0] Exit");

            System.out.print("Enter your choice: ");
            int forgotChoice = sc.nextInt();
            sc.nextLine();

            switch (forgotChoice) {
                case 1:
                    forgotCustomerID();
                    break;

                case 2:
                    visitorRegistration system = new visitorRegistration();
                    system.register(0);
                    break;

                case 0:
                    System.out.println("System exiting....\nThank you!!");
                    System.exit(0);
            }
        }
    }
}
