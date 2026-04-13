import java.util.Scanner;

public class CRMSystem {

    static Repository repo = Repository.getInstance();

    public static void showCRM(int customerID) {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Theme Park Resort Customer Support ===");
            System.out.println("[1] Customer Profile");
            System.out.println("[2] Redeem Points");
            System.out.println("[3] Cancel Appointment");
            System.out.println("[4] Move Appointment");
            System.out.println("[0] Back to Portal");
            System.out.print("Select an option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    showProfile(customerID);
                    break;
                case 2:
                    redeemPoints(customerID);
                    break;
                case 3:
                    cancelAppointment(customerID);
                    break;
                case 4:
                    moveAppointment(customerID);
                    break;
                case 0:
                    System.out.println("Returning to Portal...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ── PROFILE ──
    private static void showProfile(int customerID) {
        System.out.println("\n=== CUSTOMER PROFILE ===");
        System.out.println("Customer ID : " + customerID);
        System.out.println("Name        : " + repo.getCustomerName(customerID));
        System.out.println("Membership  : " + repo.getMembershipType(customerID));
        System.out.println("Points      : " + repo.getPoints(customerID) + " pts");
        System.out.println("Freebies    : " + repo.getFreebiesCount(customerID));
        System.out.println("------------------------");
        System.out.println("Transaction History:");
        transactionHistory.showHistory(customerID);
    }

    // ── REDEEM POINTS ──
    private static void redeemPoints(int customerID) {
        Scanner sc = new Scanner(System.in);
        int points = repo.getPoints(customerID);

        System.out.println("\n=== REDEEM POINTS ===");
        System.out.println("Current Points: " + points + " pts");
        System.out.println("-------------------------");
        System.out.println("[1] Regular Ticket (500 pts)");
        System.out.println("[2] VIP Ticket     (1000 pts)");
        System.out.println("[0] Cancel");
        System.out.print("Choose: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            if (points >= 500) {
                repo.deductPoints(customerID, 500);
                System.out.println("✔ Redeemed 1 Free Regular Ticket!");
                System.out.println("Remaining Points: " + (points - 500) + " pts");
            } else {
                System.out.println("✘ Not enough points. Need " + (500 - points) + " more pts.");
            }
        } else if (choice == 2) {
            if (points >= 1000) {
                repo.deductPoints(customerID, 1000);
                System.out.println("✔ Redeemed 1 Free VIP Ticket!");
                System.out.println("Remaining Points: " + (points - 1000) + " pts");
            } else {
                System.out.println("✘ Not enough points. Need " + (1000 - points) + " more pts.");
            }
        } else {
            System.out.println("Redemption cancelled.");
        }
    }

    // ── CANCEL APPOINTMENT ──
    private static void cancelAppointment(int customerID) {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n=== CANCEL APPOINTMENT ===");
        appointmentSystem.showAppointments(customerID);

        System.out.print("Enter appointment number to cancel (0 to go back): ");
        int num = sc.nextInt();
        sc.nextLine();

        if (num == 0) return;

        boolean cancelled = appointmentSystem.cancelAppointment(customerID, num);
        if (cancelled) {
            System.out.println("✔ Appointment cancelled successfully.");
        } else {
            System.out.println("✘ Invalid appointment number.");
        }
    }

    // ── MOVE APPOINTMENT ──
    private static void moveAppointment(int customerID) {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n=== MOVE APPOINTMENT ===");
        appointmentSystem.showAppointments(customerID);

        System.out.print("Enter appointment number to move (0 to go back): ");
        int num = sc.nextInt();
        sc.nextLine();

        if (num == 0) return;

        String newDate = appointmentSystem.pickDate();
        boolean moved = appointmentSystem.moveAppointment(customerID, num, newDate);
        if (moved) {
            System.out.println("✔ Appointment moved to " + newDate);
        } else {
            System.out.println("✘ Invalid appointment number.");
        }
    }
}
