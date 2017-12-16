package Clients;
import BankingOperationsApp.BankingOperations;
import BankingOperationsApp.BankingOperationsHelper;
import Log.Log;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
public class MangerClient {



    public static void main(String[] args) throws Exception {



        //populate();
        populate();
        startManagerOperations();

    }

    private static void startManagerOperations() {


        Scanner kb = new Scanner(System.in);
        String managerID = null;
        boolean lock = false;


        while (!lock) {

            System.out.println("Hi, please enter your ID to access the appropriate serve");
            managerID = kb.next();
            System.out.println("You've entered " + managerID);
            if (managerID.charAt(2) == 'M' || managerID.charAt(2) == 'm' ) {
                init_Sever(managerID);
            }
            else
                System.out.println("Invalid entry, must be a manager");
            lock = false;

        }
    }

    private static void init_Sever(String managerID) {


        String serverName = managerID.substring(0, 2).toUpperCase();

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
                readyForOperation(bankingOperations, managerID);

            } catch (Exception e) {
                System.out.println("Client exception: " + e);
                e.printStackTrace();
            }

        } else {

            System.out.println("Manager's server name invalid, please log back again with the correct ID");
            startManagerOperations();


        }

    }

    private static void readyForOperation(BankingOperations server, String managerID) {


        int optionSelected = 0;
        String managerServer = managerID.substring(0,2);
        Scanner kb = new Scanner(System.in);


        do {
            System.out.println("Hi " + managerID + " what would you like to do today?");
            System.out.println("\n1.Create new account.\n2.Edit record.\n3.Get an account count.\n4.Deposit" +
                    " \n5.Withdraw \n6.Get balance.\n7.Transfer balance \n8.Exit");
            optionSelected = kb.nextInt();

            switch (optionSelected) {

                case 1:
                    System.out.println("You've selected create new account!Enter - Fname, lname, address, phone, branch");
                    String fName = kb.next();
                    String lName = kb.next();
                    String address = kb.next();
                    String phone = kb.next();
                    String branch = kb.next();
                    kb.nextLine();


                    if (Objects.equals(managerServer, branch)) {
                        boolean accCreate = server.createAccountRecord(fName, lName, address, phone, branch);
                        if (accCreate) {
                            System.out.println("Account created!");
                            try {

                                Log.generateLogFileClient("Account created ", "Manager", managerID);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            System.out.println("Account was not created. Please try again");
                            try {
                                Log.generateLogFileClient("Account creation failed ", "Manager", managerID);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        System.out.println("Must be the same branch as the manager using the server!");
                        try {
                            Log.generateLogFileClient("Account creation failed ", "Manager", managerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    System.out.println("--------------------------");

                    break;


                case 2:
                    System.out.println("You've selected edit record. Enter customerID, field and new value!");
                    String custID = kb.next();
                    String field = kb.next();
                    String newValue = kb.next();
                    kb.nextLine();


                    boolean editAcc = server.editRecord(custID, field, newValue);

                    if (editAcc) {
                        System.out.println("Account edited!");
                        try {
                            Log.generateLogFileClient("Account edited ", "Manager", managerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Problem with editing account!");
                        try {
                            Log.generateLogFileClient("Account created ", "Manager", managerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("--------------------------");

                    break;


                case 3:
                    System.out.println("Printing the current");
                    String[] clientCount = server.getAccountCount();
                    System.out.println("--------------------------");
                    for (String aClientCount : clientCount) {
                        System.out.println(aClientCount);
                    }

                    try {
                        Log.generateLogFileClient("Get account count ", "Manager", managerID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("--------------------------");

                    break;
                case 4:
                    System.out.println("You've selected Deposit. Enter customerID, and amount to deposit!");
                    String custIDDep = kb.next();
                    double dep = kb.nextDouble();
                    kb.nextLine();


                    boolean deposit = server.deposit(custIDDep, dep);

                    if (deposit) {
                        System.out.println("Successful deposit");
                        double getDepBalance = server.getBalance(custIDDep);
                        System.out.println("New balance $" + getDepBalance);
                        try {
                            Log.generateLogFileClient("Deposit successful ", "Manager", managerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Problem with deposit!");
                        try {
                            Log.generateLogFileClient("Deposit failed ", "Manager", managerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("--------------------------");

                    break;

                case 5:
                    System.out.println("You've selected withdraw! Enter customerID, and amount to withdraw!");

                    String custIDWith = kb.next();
                    double withdrw = kb.nextDouble();
                    kb.nextLine();

                    boolean withdraw = server.withdraw(custIDWith, withdrw);

                    if (withdraw) {
                        System.out.println("Successful Withdraw!");
                        double getDepBalance = server.getBalance(custIDWith);
                        System.out.println("New balance $" + getDepBalance);
                        try {
                            Log.generateLogFileClient("Withdraw success ", "Manager", managerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Problem with withdraw!");
                        try {
                            Log.generateLogFileClient("Withdraw failed ", "Manager", managerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("--------------------------");

                    break;
                case 6:
                    System.out.println("Enter the customer to see balance");
                    String custBal = kb.next();
                    kb.nextLine();
                    double checkBalance = server.getBalance(custBal);
                    System.out.println("Current balance $" + checkBalance);
                    try {
                        Log.generateLogFileClient("Balance check ", "Manager", managerID);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    System.out.println("--------------------------");
                    break;
                case 7:
                    System.out.println("You've selected transfer money! Enter the Source account, destination account and amount to send!");
                    //String managerID, double amount, String sourceCustomerID, String destinationCustomerID
                    String sourceAcc = kb.next();
                    String destinationAcc = kb.next();
                    double amountToSned = kb.nextDouble();
                    boolean transfer = server.transferFund(managerID, amountToSned, sourceAcc, destinationAcc);

                    if (transfer) {
                        System.out.println("Transfer to " + destinationAcc + " complete");
                        try {
                            Log.generateLogFileClient("Transfer balance to " + destinationAcc
                                    + " from " + sourceAcc, "Manager", managerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        System.out.println("Transfer failed");
                        try {
                            Log.generateLogFileClient("Transfer balance to " + destinationAcc
                                    + " from " + sourceAcc + " failed", "Manager", managerID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }


                    System.out.println("--------------------------");
                    break;
                case 8:
                    System.out.println("Logging out");
                    System.out.println("Bye! " + managerID);
                    System.out.println("--------------------------");
                    startManagerOperations();
                    break;


            }

        } while (true);

    }


    private static void populate() throws Exception {

        String[] serverName = {"QC", "BC" , "NB" , "MB"};

        String[] args = {"-ORBInitialPort", "1050", "-ORBInitialHost", "localhost"};

        ORB orb = ORB.init(args, null);
        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
        BankingOperations bankingOperations = BankingOperationsHelper.narrow(ncRef.resolve_str(serverName[0]));




        boolean good = bankingOperations.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"QC");
        boolean good2 = bankingOperations.createAccountRecord("Bla" , "Hetfield", "3025 Antoine", "5141111111" ,"QC");
        boolean good3 = bankingOperations.createAccountRecord("int" , "Hetfield", " St. Antoine", "5141111111" ,"QC");

        System.out.println(good);

        boolean wa = bankingOperations.editRecord("QCC1000", "Phone", "999" );


       bankingOperations = BankingOperationsHelper.narrow(ncRef.resolve_str(serverName[1]));


        boolean asd2 = bankingOperations.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"BC");
        boolean asd222 = bankingOperations.createAccountRecord("Bla" , "Hetfield", "3025 Antoine", "5141111111" ,"BC");
        boolean asd322 = bankingOperations.createAccountRecord("int" , "Hetfield", " St. Antoine", "5141111111" ,"BC");
        boolean qwfq22 = bankingOperations.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"BC");


        bankingOperations = BankingOperationsHelper.narrow(ncRef.resolve_str(serverName[2]));


        boolean asdds = bankingOperations.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"NB");
        boolean asd2ds = bankingOperations.createAccountRecord("Bla" , "Hetfield", "3025 Antoine", "5141111111" ,"NB");
        boolean asd3ds = bankingOperations.createAccountRecord("int" , "Hetfield", " St. Antoine", "5141111111" ,"NB");
        boolean qwfqds = bankingOperations.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"NB");
        boolean asd4ds = bankingOperations.createAccountRecord("Bla" , "Hetfield", "3025 Antoine", "5141111111" ,"NB");





        bankingOperations = BankingOperationsHelper.narrow(ncRef.resolve_str(serverName[3]));


        boolean asddas = bankingOperations.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"MB");
        boolean asd3aa = bankingOperations.createAccountRecord("int" , "Hetfield", " St. Antoine", "5141111111" ,"MB");
        boolean qwfqaa = bankingOperations.createAccountRecord("James" , "Hetfield", "3025 St. Antoine", "5141111111" ,"MB");
        boolean asd4aa = bankingOperations.createAccountRecord("Bla" , "Hetfield", "3025 Antoine", "5141111111" ,"MB");
        boolean asd5aa = bankingOperations.createAccountRecord("int" , "Hetfield", " St. Antoine", "5141111111" ,"MB");


        bankingOperations = BankingOperationsHelper.narrow(ncRef.resolve_str(serverName[0]));
        bankingOperations.getAccountCount();

        bankingOperations = BankingOperationsHelper.narrow(ncRef.resolve_str(serverName[1]));
        bankingOperations.getAccountCount();

        bankingOperations = BankingOperationsHelper.narrow(ncRef.resolve_str(serverName[2]));
        bankingOperations.getAccountCount();

        bankingOperations = BankingOperationsHelper.narrow(ncRef.resolve_str(serverName[3]));
        bankingOperations.getAccountCount();




    }


}
