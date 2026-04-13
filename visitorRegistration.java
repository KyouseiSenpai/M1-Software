import java.util.Scanner;
import java.time.LocalDate;

public class visitorRegistration {

    static Repository repo = Repository.getInstance();
    public void register(int age) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.println("--Welcome to Theme Park Resort!!--\nKindly fill up our form to proceed!!");

            System.out.print("\nEnter your full name: ");
            String fullName = sc.nextLine();

            System.out.print("Enter your contact number: ");
            String contactNumber = sc.nextLine();

            int newID = repo.generateCustomerID();
            repo.saveCustomer(newID, fullName, contactNumber, age);

            System.out.println("Registration successful! Your Customer ID is: " + newID);
            System.out.println("Please remember your Customer ID for future transactions.\n");

            membership.membershipUpgrade(newID);

            int regChoice;
            do {
                System.out.println("What do you wanna do next?");
                System.out.println("[1] Proceed to Log In");
                System.out.println("[0] Exit");
                System.out.print("\nEnter your choice: ");
                regChoice = sc.nextInt();
                sc.nextLine();

                switch (regChoice) {
                    case 1:
                        System.out.println("Proceeding to Log In Menu...");
                        visitorLogIn.login();
                        break;

                    case 0:
                        System.out.println("System exiting....\nThank you!!");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid choice, please try again.");
                        break;
                }
            } while (regChoice != 0);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void readAgeVerification() {
        Scanner sc = new Scanner(System.in);

        int avChoice;
        System.out.println("The system checks the age of the customer during registration. Customers under 16 years old are not allowed to register unless accompanied by an adult or guardian.");
        System.out.println("[1] Back");
        System.out.println("[0] Exit");

        System.out.print("Enter your choice: ");
        avChoice = sc.nextInt();
        sc.nextLine();

        switch (avChoice) {
            case 1:
                break;

            case 0:
                System.out.println("System exiting....\nThank you!!");
                System.exit(0);
                break;

            default:
                System.out.println("Invalid choice, please try again.");
                readAgeVerification();
                break;
        }
    }

    public visitorRegistration() {}

    private int customerID;
    private String fullName;
    private String contactNumber;
    private String accountDateCreated;
    private String membershipStatus;

    public visitorRegistration(int customerID, String fullName, String contactNumber, String accountDateCreated, String membershipStatus) {
        this.customerID = customerID;
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.accountDateCreated = accountDateCreated;
        this.membershipStatus = membershipStatus;
    }
}
