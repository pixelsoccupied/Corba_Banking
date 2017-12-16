package Clients;

import BankingOperationsApp.BankingOperations;
import BankingOperationsApp.BankingOperationsHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import Log.Log;

public class CustomerClient {

    public static HashMap<String, String[]>portArg = new HashMap<>();


    private String firstName;
    private String lastName;
    private String accountNumber;
    private String address;
    private String phone;
    private double balance;
    private String branch;

    public CustomerClient(String firstName, String lastName, String accountNumber, String address, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountNumber = accountNumber;
        this.address = address;
        this.phone = phone;
    }

    public CustomerClient(String firstName, String lastName, String accountNumber, String address, String phone, String branch) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountNumber = accountNumber;
        this.address = address;
        this.phone = phone;
        this.branch = branch;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return "Customer -----------------" +
                "\nFirst Name - " + firstName +
                "\nLast Name - " + lastName +
                "\nAccount Number - " + accountNumber +
                "\nAddress - " + address +
                "\nPhone #" + phone +
                "\nBalance $" + balance +
                "\nBranch -" + branch +
                "\n-------------";
    }

    public static void main(String[] args) throws Exception {


        startClientOperations();

    }



    private static void startClientOperations() {

        Scanner kb = new Scanner(System.in);


        String customerID;
        boolean lock = false;


        while (!lock) {

            System.out.println("Hi, please enter your ID to access the appropriate serve");
            customerID = kb.next();
            System.out.println("You've entered " + customerID);
            if (customerID.charAt(2) == 'C' || customerID.charAt(2) == 'c' ) {
                init_Sever(customerID);
            }
            else
                System.out.println("Invalid entry, must be a customer!");


        }
    }

    private static void init_Sever(String customerID) {


        String serverName = customerID.substring(0, 2).toUpperCase();

        System.out.println(serverName);


        if (serverName.equalsIgnoreCase("qc") || serverName.equalsIgnoreCase("bc") || serverName.equalsIgnoreCase("mb")
                || serverName.equalsIgnoreCase("nb")) {

            try {

                String[] args = {"-ORBInitialPort", "1050", "-ORBInitialHost", "localhost"};

                ORB orb = ORB.init(args, null);
                org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
                NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
                BankingOperations bankingOperations = BankingOperationsHelper.narrow(ncRef.resolve_str(serverName));
                System.out.println("Taking you to the correct server...");
                readyForOperation(bankingOperations, customerID);

            } catch (Exception e) {
                System.out.println("Client exception: " + e);
                e.printStackTrace();
            }

        } else {

            System.out.println("Customer's server name invalid, please log back again with the correct ID");
            startClientOperations();


        }
    }

    private static void readyForOperation(BankingOperations server, String customerID) {

        int optionSelected = 0;
        String customerServer = customerID.substring(0, 2);
        Scanner kb = new Scanner(System.in);


        do {

            System.out.println("Hi " + customerID + " what would you like to do today?");
            System.out.println("\n1.Deposit" + " \n2.Withraw \n3.Get balance.\n4.Transfer balance \n5.Exit");
            optionSelected = kb.nextInt();

            switch (optionSelected) {

                case 1:
                    System.out.println("You've selected Deposit. Enter amount to deposit!");
                    double dep = kb.nextDouble();
                    kb.nextLine();


                    boolean deposit = server.deposit(customerID, dep);

                    if (deposit) {
                        System.out.println("Successful deposit");
                        double getDepBalance = server.getBalance(customerID);
                        System.out.println("New balance $" + getDepBalance);
                        try {
                            Log.generateLogFileClient("Deposit ", "Customer", customerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Problem with deposit!");
                        try {
                            Log.generateLogFileClient("Deposit failed ", "Customer", customerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println("--------------------------");

                    break;

                case 2:
                    System.out.println("You've selected withdraw! Enter amount to withdraw!");


                    double withdrw = kb.nextDouble();
                    kb.nextLine();
                    double currentBal = server.getBalance(customerID);
                    if (withdrw < currentBal ) {
                        boolean withdraw = server.withdraw(customerID, withdrw);

                        if (withdraw) {
                            System.out.println("Successful Withdraw!");
                            double getDepBalance = server.getBalance(customerID);
                            System.out.println("New balance $" + getDepBalance);
                            try {
                                Log.generateLogFileClient("Withdraw ", "Customer", customerID);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else
                            System.out.println("Problem with withdraw!");

                        try {
                            Log.generateLogFileClient("Withdraw failed  ", "Customer", customerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    else {
                        try {
                            Log.generateLogFileClient("Not enough $$ ", "Customer", customerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Sorry you don't have enough money to withdraw. Please try a smaller amount");

                    }
                    break;
                case 3:

                    double checkBalance = server.getBalance(customerID);
                    System.out.println("Current balance $" + checkBalance);
                    try {
                        Log.generateLogFileClient("Checked balance ", "Customer", customerID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("--------------------------");
                    break;
                case 4:
                    System.out.println("You've selected transfer money! Enter the destination account followed by the amount to send!");
                    //String managerID, double amount, String sourceCustomerID, String destinationCustomerID
                    String destinationAcc = kb.next();
                    double amountToSned = kb.nextDouble();
                    boolean transfer = server.transferFund("Auto" , amountToSned, customerID, destinationAcc);

                    if(transfer){
                        System.out.println("Transfer to " + destinationAcc + " complete");
                        try {
                            Log.generateLogFileClient("Tranfer balance to " + destinationAcc, "Customer", customerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else {
                        System.out.println("Transfer failed");
                        try {
                            Log.generateLogFileClient("Tranfer balance to " + destinationAcc + " failed", "Customer", customerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }



                    System.out.println("--------------------------");
                    break;
                case 5:
                    System.out.println("Logging out");
                    System.out.println("Bye! " + customerID);
                    System.out.println("--------------------------");
                    startClientOperations();
                    break;


            }


        } while (true);










    }


}
