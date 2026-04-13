import java.util.Scanner;

public class checkIn {

    static Repository repo = Repository.getInstance();

    public static double totalAmount = 0;
    public static int totalPoints = 0;
    public static StringBuilder receiptLines = new StringBuilder();

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

    public static void startCheckIn(int customerID, QueueChecker queueChecker) {
        Scanner sc = new Scanner(System.in);
        String membershipType = repo.getMembershipType(customerID);
        boolean isVIP = "VIP".equals(membershipType);

        totalAmount = 0;
        totalPoints = 0;
        receiptLines.setLength(0);

        // ── ACCOUNT HOLDER TICKET ──
        System.out.println("\n=== BUY TICKET (Your Ticket) ===");
        System.out.println("[1] Regular Ticket - PHP 500.00");
        System.out.println("[2] VIP Ticket     - PHP 800.00");
        System.out.print("Choose: ");
        int choice = sc.nextInt();
        sc.nextLine();

        String ticketName = repo.getCustomerName(customerID);
        int ticketAge = repo.getCustomerAge(customerID);
        System.out.println("Ticket holder: " + ticketName + " (Age: " + ticketAge + ")");

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
            int freebies = repo.getFreebiesCount(customerID);
            if (isVIP && freebies > 0) {
                price -= 300;
                repo.useFreebies(customerID);
                System.out.println("★ Freebie applied! -PHP 300.00");
            }
            points = isVIP ? 200 : 100;
        }

        double originalPrice = price;
        price = getAgeDiscount(ticketAge, price);
        String ageCategory = getAgeCategory(ticketAge);

        if (price < originalPrice)
            System.out.printf("★ Age discount (%s): PHP %.2f → PHP %.2f%n", ageCategory, originalPrice, price);

        repo.saveTicketRecord(customerID, ticketName, ticketAge);
        repo.updateTicketSummary(customerID, price);

        totalAmount += price;
        totalPoints += points;
        receiptLines.append(String.format(
                "  > %-20s | %-15s | %s | PHP %.2f | +%d pts%n",
                ticketName, ageCategory, label, price, points));

        System.out.printf("✔ Your ticket added | +%d pts%n", points);

        // ── GUEST TICKETS ──
        System.out.print("\nWould you like to buy a ticket for someone else?\n" +
                " [1] Yes\n [2] No - Proceed to Check Out\n");
        System.out.print("Enter your choice: ");
        int again = sc.nextInt();
        sc.nextLine();

        if (again == 1) {
            System.out.print("How many tickets do you want to buy? ");
            int personCount = sc.nextInt();
            sc.nextLine();

            for (int i = 1; i <= personCount; i++) {
                System.out.println("\n=== GUEST " + i + " of " + personCount + " ===");

                System.out.print("Ticket holder name: ");
                String guestName = sc.nextLine();

                System.out.print("Ticket holder age: ");
                int guestAge = sc.nextInt();
                sc.nextLine();

                System.out.println("[1] Regular Ticket - PHP 500.00");
                System.out.println("[2] VIP Ticket     - PHP 800.00");
                System.out.print("Choose: ");
                int guestChoice = sc.nextInt();
                sc.nextLine();

                double guestPrice;
                String guestLabel;
                int guestPoints;

                if (guestChoice == 1) {
                    guestPrice = 500.00;
                    guestLabel = "Regular Ticket";
                    guestPoints = isVIP ? 100 : 50;
                } else {
                    guestPrice = 800.00;
                    guestLabel = "VIP Ticket";
                    guestPoints = isVIP ? 200 : 100;
                }

                double originalGuestPrice = guestPrice;
                guestPrice = getAgeDiscount(guestAge, guestPrice);
                String guestCategory = getAgeCategory(guestAge);

                if (guestPrice < originalGuestPrice)
                    System.out.printf("★ Age discount (%s): PHP %.2f → PHP %.2f%n",
                            guestCategory, originalGuestPrice, guestPrice);

                repo.saveTicketRecord(customerID, guestName, guestAge);
                repo.updateTicketSummary(customerID, guestPrice);

                totalAmount += guestPrice;
                totalPoints += guestPoints;
                receiptLines.append(String.format(
                        "  > %-20s | %-15s | %s | PHP %.2f | +%d pts%n",
                        guestName, guestCategory, guestLabel, guestPrice, guestPoints));

                System.out.printf("✔ Ticket added for %s (%s) | +%d pts%n",
                        guestName, guestCategory, guestPoints);
            }
        }

        System.out.println("\n========== SUMMARY ==========");
        System.out.print(receiptLines);
        System.out.println("-----------------------------");
        System.out.printf("Total Amount : PHP %.2f%n", totalAmount);
        System.out.printf("Total Points : %d pts%n", totalPoints);
        System.out.println("=============================");

        System.out.println("\n=== PAYMENT ===");
        System.out.println("[1] Online Payment");
        System.out.println("[2] Walk-In (Pay at Counter)");
        System.out.print("Choose: ");
        int payChoice = sc.nextInt();
        sc.nextLine();

        String paymentStatus;

        if (payChoice == 1) {
            System.out.println("\n[1] GCash  [2] Maya  [3] Card  [4] Online Banking");
            System.out.print("Choose: ");
            int method = sc.nextInt();
            sc.nextLine();

            String[] methods = {"GCash", "Maya", "Card", "Online Banking"};
            String chosen = methods[method - 1];

            System.out.println("\nProceed to payment via " + chosen + "?");
            System.out.printf("Amount to pay: PHP %.2f%n", totalAmount);
            System.out.print("Confirm [1] Yes [2] No: ");
            int confirm = sc.nextInt();
            sc.nextLine();

            if (confirm == 1) {
                System.out.println("✔ Payment confirmed via " + chosen + "!");
                paymentStatus = "PAID";
                queueChecker.checkInClass(customerID, "Theme Park Entry"); // ← queue only here
            } else {
                System.out.println("Payment cancelled. Defaulting to Walk-In.");
                paymentStatus = "PENDING";
                repo.updateQueueStatus(customerID, "CANCELLED"); // ← clear queue
            }
        } else {
            System.out.println("✔ Please pay at the counter on your appointment date.");
            paymentStatus = "PENDING";
            repo.updateQueueStatus(customerID, "CANCELLED"); // ← clear queue
        }

        String appointmentDate = appointmentSystem.pickDate();
        appointmentSystem.saveAppointment(customerID, appointmentDate, paymentStatus);
        repo.saveAppointmentRecord(customerID, appointmentDate, paymentStatus);

        repo.loyaltyPoints(customerID, totalPoints);
        checkOut.showReceipt(customerID, membershipType, receiptLines,
                totalAmount, totalPoints, appointmentDate, paymentStatus);
    }
}