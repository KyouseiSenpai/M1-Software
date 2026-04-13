import java.util.Scanner;

public class membership extends visitorRegistration {

    static Repository repo = Repository.getInstance();
    public static void membershipUpgrade(int newID) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Would you like to upgrade to VIP membership now?");
        System.out.println("This upgrade includes your ticket membership type (1 time only).");
        System.out.println("VIP Benefits:");
        System.out.println("  - 20% discount on all services (Permanent)");
        System.out.println("  - Priority Lane access (Permanent)");
        System.out.println("  - VIP Lounge, Early Entry & Exclusive Events (Freebies)");
        System.out.println("  - 2x Points on every transaction (Permanent)");
        System.out.println("Upgrade fee: PHP 150\n");
        System.out.println("[1] Yes, upgrade to VIP");
        System.out.println("[2] No, stay as Regular");
        System.out.print("Enter your choice: ");
        int upgradeChoice = sc.nextInt();
        sc.nextLine();

        if (upgradeChoice == 1) {
            String current = repo.getMembershipType(newID);

            if ("VIP".equals(current)) {
                System.out.println("You are already a VIP member!");
            } else {
                repo.updateMembershipType(newID, "VIP");
                System.out.println("★ Thank you for upgrading! Your Membership Status is now: VIP");
                System.out.println("★ You have received 1 freebies count as a VIP benefit!");
            }
        } else {
            System.out.println("Upgrade was not completed. You remain a Regular member.");
            System.out.println("You can upgrade anytime from the main portal.");
        }
    }

    public static void handleQueueAndDiscount(int customerID, double totalAmount) {
        String membershipType = repo.getMembershipType(customerID);

        if ("VIP".equals(membershipType)) {
            System.out.println("=== VIP ACCESS ===");
            System.out.println(">> Proceeding directly to counter. No queue!");

            double discount = totalAmount * 0.20;
            double finalAmount = totalAmount - discount;
            System.out.printf("Original Amount : P%.2f%n", totalAmount);
            System.out.printf("Discount (20%%) : -P%.2f%n", discount);
            System.out.printf("Final Amount    : P%.2f%n", finalAmount);

        } else {
            System.out.println("=== REGULAR ACCESS ===");
            System.out.println(">> Please proceed to the queue.");
            System.out.printf("Total Amount: P%.2f%n", totalAmount);
            System.out.println("(No discount available for Regular members)");
        }
    }
}
