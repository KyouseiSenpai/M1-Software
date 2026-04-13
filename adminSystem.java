import java.util.Scanner;

public class adminSystem {

    private static final String ADMIN_PASSWORD = "Admin123";

    public static void showAdminLogin(Scanner sc) {
        System.out.print("Enter staff password: ");
        String password = sc.nextLine();

        if (password.equals(ADMIN_PASSWORD)) {
            showDashboard(sc);
        } else {
            System.out.println("Access denied. Returning to main menu...");
        }
    }

    private static void showDashboard(Scanner sc) {
        int adminChoice;
        do {
            System.out.println("\n== Staff Dashboard ==");
            System.out.println("[1] Accommodate Customer");
            System.out.println("[2] Reservations Control");
            System.out.println("[0] Back to Main Menu");
            System.out.print("Enter your choice: ");
            adminChoice = sc.nextInt();
            sc.nextLine();

            switch (adminChoice) {
                case 1:
                    accommodateCustomer(sc);
                    break;
                case 2:
                    reservationsControl(sc);
                    break;
                case 0:
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        } while (adminChoice != 0);
    }

    private static void accommodateCustomer(Scanner sc) {
        int accChoice;
        do {
            System.out.println("\n== Accommodate Customer ==");
            System.out.println("[1] Search Customer (information checker)");
            System.out.println("[2] Walk-in (buy ticket, create account, upgrade membership)");
            System.out.println("[0] Back to Staff Dashboard");
            System.out.print("Enter your choice: ");
            accChoice = sc.nextInt();
            sc.nextLine();

            switch (accChoice) {
                case 1:
                    searchAllCustomers();
                    break;
                case 2:
                    handleWalkIn(sc);
                    break;
                case 0:
                    System.out.println("Returning to Staff Dashboard...");
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        } while (accChoice != 0);
    }

    private static void searchAllCustomers() {
        String query = "SELECT customerID, customerFullName, customerContactNumber, customerAge, accountDateCreated, dateTimeIn, dateTimeOut " +
                "FROM tbl_customerDetails";

        try (java.sql.Connection conn = new Repository.RepositoryBuilder().setDatabasePath().build().getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("---------------------------------------------------------------------------------------------------");
            System.out.printf("%-10s %-20s %-15s %-5s %-15s %-20s %-15s%n",
                    "ID", "Name", "Contact", "Age", "Created", "Logged In", "Status");
            System.out.println("---------------------------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("customerID");
                String name = rs.getString("customerFullName");
                String contact = rs.getString("customerContactNumber");
                int age = rs.getInt("customerAge");
                String created = rs.getString("accountDateCreated");
                String loggedIn = rs.getString("dateTimeIn");
                String loggedOut = rs.getString("dateTimeOut");

                String status;
                if (loggedIn != null && (loggedOut == null || loggedOut.isEmpty())) {
                    status = "Logged In";
                } else if (loggedOut != null && !loggedOut.isEmpty()) {
                    status = "Logged Out";
                } else {
                    status = "Not Yet Logged In";
                }

                System.out.printf("%-10d %-20s %-15s %-5d %-15s %-20s %-15s%n",
                        id, name, contact, age, created, (loggedIn != null ? loggedIn : "-"), status);
            }

            if (!found) System.out.println("No customers found in the database.");
            System.out.println("---------------------------------------------------------------------------------------------------");

        } catch (java.sql.SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static final Repository repo = new Repository.RepositoryBuilder().setDatabasePath().build();
    private static double totalAmount;
    private static int totalPoints;
    private static StringBuilder receiptLines = new StringBuilder();

    private static void handleWalkIn(Scanner sc) {
        int choice;
        do {
            System.out.println("\n== Walk-in Customer ==");
            System.out.println("[1] Buy Ticket");
            System.out.println("[2] Create Account");
            System.out.println("[3] Upgrade Membership");
            System.out.println("[0] Back to Accommodate Menu");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    startCheckIn(sc);
                    break;
                case 2:
                    createAccount(sc);
                    break;
                case 3:
                    upgradeMembership(sc);
                    break;
                case 0:
                    System.out.println("Returning to Accommodate Menu...");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 0);
    }

    private static void startCheckIn(Scanner sc) {
        System.out.print("Enter Customer ID: ");
        int customerID = sc.nextInt();
        sc.nextLine();

        String membership = repo.getMembershipType(customerID);
        boolean isVIP = "VIP".equals(membership);
        boolean buyMore = true;

        totalAmount = 0;
        totalPoints = 0;
        receiptLines.setLength(0);

        while (buyMore) {
            System.out.println("\n=== BUY TICKET ===");
            System.out.println("[1] Regular Ticket - PHP 500.00");
            System.out.println("[2] VIP Ticket     - PHP 800.00");
            System.out.print("Choose: ");
            int choice = sc.nextInt();
            sc.nextLine();

            double price;
            String label;
            int points;

            if (choice == 1) {
                price = 500.00;
                label = "Regular Ticket";
                points = isVIP ? 100 : 50;
            } else {
                price = 800.00;
                label = "VIP Ticket";
                Integer freebies = repo.getFreebiesCount(customerID);
                if (isVIP && freebies != null && freebies > 0) {
                    price -= 300;
                    System.out.println("★ Freebie applied! -PHP 300.00");
                }
                points = isVIP ? 200 : 100;
            }

            System.out.print("Enter Age for discount: ");
            int age = sc.nextInt();
            sc.nextLine();
            double finalPrice = getAgeDiscount(age, price);
            String category = getAgeCategory(age);

            totalAmount += finalPrice;
            totalPoints += points;
            receiptLines.append(String.format("%s (%s) : P%.2f%n", label, category, finalPrice));

            System.out.print("\nBuy another ticket? [1] Yes [2] No: ");
            int again = sc.nextInt();
            sc.nextLine();
            buyMore = (again == 1);
        }

        repo.loyaltyPoints(customerID, totalPoints);
        System.out.println("\n=== RECEIPT ===");
        System.out.println(receiptLines.toString());
        System.out.printf("TOTAL AMOUNT : P%.2f%n", totalAmount);
        System.out.printf("POINTS EARNED: %d%n", totalPoints);
    }

    private static void reservationsControl(Scanner sc) {
        int choice;
        do {
            System.out.println("\n== Reservations Control ==");
            System.out.println("[1] Add Reservation");
            System.out.println("[2] Search Reservation");
            System.out.println("[0] Back to Staff Dashboard");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addReservation(sc);
                    break;
                case 2:
                    searchReservation(sc);
                    break;
                case 0:
                    System.out.println("Returning to Staff Dashboard...");
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        } while (choice != 0);
    }

    private static void addReservation(Scanner sc) {
        System.out.print("Enter Customer ID: ");
        int customerID = sc.nextInt();
        sc.nextLine();

        String name = repo.getCustomerName(customerID);
        if ("Unknown".equals(name)) {
            System.out.println("✘ Customer not found.");
            return;
        }

        System.out.println("Customer: " + name);
        String date = appointmentSystem.pickDate();
        appointmentSystem.saveAppointment(customerID, date, "PENDING");
        repo.saveAppointmentRecord(customerID, date, "PENDING");
        System.out.println("✔ Reservation added for " + name + " on " + date);
    }

    private static void searchReservation(Scanner sc) {
        System.out.print("Enter Customer ID: ");
        int customerID = sc.nextInt();
        sc.nextLine();

        String name = repo.getCustomerName(customerID);
        if ("Unknown".equals(name)) {
            System.out.println("✘ Customer not found.");
            return;
        }

        System.out.println("Customer: " + name);
        appointmentSystem.showAppointments(customerID);

        int choice;
        do {
            System.out.println("\n[1] Move Reservation");
            System.out.println("[2] Cancel Reservation");
            System.out.println("[0] Back");
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter appointment number to move: ");
                    int moveNum = sc.nextInt();
                    sc.nextLine();
                    String newDate = appointmentSystem.pickDate();
                    if (appointmentSystem.moveAppointment(customerID, moveNum, newDate)) {
                        System.out.println("✔ Reservation moved to " + newDate);
                    } else {
                        System.out.println("✘ Invalid appointment number.");
                    }
                    break;
                case 2:
                    System.out.print("Enter appointment number to cancel: ");
                    int cancelNum = sc.nextInt();
                    sc.nextLine();
                    if (appointmentSystem.cancelAppointment(customerID, cancelNum)) {
                        System.out.println("✔ Reservation cancelled.");
                    } else {
                        System.out.println("✘ Invalid appointment number.");
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        } while (choice != 0);
    }

    private static double getAgeDiscount(int age, double price) {
        if (age <= 3) return 0;
        if (age <= 12) return price * 0.50;
        if (age >= 60) return price * 0.80;
        return price;
    }

    private static String getAgeCategory(int age) {
        if (age <= 3) return "Infant (FREE)";
        if (age <= 12) return "Child (50% off)";
        if (age >= 60) return "Senior (20% off)";
        return "Adult";
    }

    private static void createAccount(Scanner sc) {
        System.out.println("=== Walk-in Registration ===");
        System.out.print("Enter Full Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Contact Number: ");
        String contact = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = sc.nextInt();
        sc.nextLine();

        int newID = repo.generateCustomerID();
        repo.saveCustomer(newID, name, contact, age);

        System.out.println("★ Walk-in account created successfully!");
        System.out.println("Assigned Customer ID: " + newID);
        System.out.println("Membership Status: Regular");
    }

    private static void upgradeMembership(Scanner sc) {
        System.out.print("Enter Customer ID to upgrade: ");
        int id = sc.nextInt();
        sc.nextLine();
        membership.membershipUpgrade(id);
    }
}