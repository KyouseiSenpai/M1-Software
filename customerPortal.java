import java.util.Scanner;

public class customerPortal extends visitorRegistration {

    static Repository repo = Repository.getInstance();
    static QueueChecker queueChecker = new QueueChecker();

    public static void portalMenu(int customerID) {

        Scanner sc = new Scanner(System.in);
        int checkInChoice = -1;

        do {
            System.out.println("====================================");
            System.out.println("THEME PARK RESORT RECEPTION SYSTEM");
            System.out.println("====================================");

            System.out.println("\nWelcome to the Reception Service Portal");
            System.out.println("Please choose a service:");

            System.out.println("[1] Check-In / Buy Ticket");
            System.out.println("[2] Check Queue");
            System.out.println("[3] Customer Relationship Services");
            System.out.println("[4] Upgrade Membership");
            System.out.println("[5] My Appointments");
            System.out.println("[0] Logout");

            System.out.print("Enter your choice: ");
            checkInChoice = sc.nextInt();
            sc.nextLine();

            switch (checkInChoice) {
                case 1:
                    checkIn.startCheckIn(customerID, queueChecker);
                    break;
                case 2:
                    queueChecker.showQueueStatus(customerID);
                    break;
                case 3:
                    CRMSystem.showCRM(customerID);
                    break;
                case 4:
                    membership.membershipUpgrade(customerID);
                    break;
                case 5:
                    appointmentSystem.showAppointments(customerID);
                    break;
                case 0:
                    System.out.println("Logging out customer ID: " + customerID);
                    queueChecker.shutdown(); // Added cleanup
                    repo.trackLogOut(customerID);
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
                    break;
            }

        } while (checkInChoice != 0);
    }
}
