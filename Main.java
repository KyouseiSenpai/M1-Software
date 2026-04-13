import java.util.Scanner;

public class Main{
    public static void main(String []args){

        Scanner sc = new Scanner(System.in);
        int choice;

        do{
        System.out.println("== Hello Customer, Welcome to Theme Park Resort!! == ");
        System.out.println("How can i help you? \n[1] Sign Up\n[2] Log In\n[0] Exit");
        System.out.print("Enter your choice: ");
        choice = sc.nextInt();
        sc.nextLine();

            switch (choice) {
                case 1:
                    int registrationChoice;
                    do {
                        System.out.println("==New Customer Registration==\n" +
                                "What would you like to do?\n" +
                                "[1] Register\n" +
                                "[2] Read Age Verification\n" +
                                "[0] Exit");

                        System.out.print("Enter your choice: ");
                        registrationChoice = sc.nextInt();
                        sc.nextLine();

                        switch (registrationChoice) {
                            case 1:
                                int age;

                                System.out.print("Please enter your age: ");
                                age = sc.nextInt();

                                if(age >= 16) {
                                    visitorRegistration vr = new visitorRegistration();
                                    vr.register(age);
                                    break;
                                } else {
                                    System.out.println("Sorry, customers under 16 years old are not allowed to register independently.\n" +
                                            "Please register with a parent or guardian.\n" +
                                            "Returning to Customer Registration Menu, please wait....\n");
                                    break;
                                }

                            case 2:
                                visitorRegistration av = new visitorRegistration();
                                av.readAgeVerification();
                                break;

                            case 0:
                                System.out.println("System exiting....\nThank you!!");
                                break;

                            default:
                                System.out.println("Invalid choice, please try again.");
                        }

                    } while (registrationChoice != 0);
                    break;


                case 2:
                    visitorLogIn.login();
                    break;

                case 9:
                    adminSystem.showAdminLogin(sc);
                    break;

                case 0:
                    System.out.println("System exiting....\nThank you!!");
                    break;

                default:
                    System.out.println("Invalid choice, try again.");
                    break;
            }

            } while(choice != 0);

            sc.close();
    }
}
