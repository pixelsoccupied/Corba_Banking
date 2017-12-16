package BankingOpsImplementation;

import BankingOperationsApp.BankingOperationsPOA;
import Clients.CustomerClient;
import org.omg.CORBA.ORB;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

import static Log.Log.generateLogFileServer;

public class NBOpsImplementation extends BankingOperationsPOA implements Runnable{

    private ORB orb;


    static private HashMap<Character, ArrayList<CustomerClient>> hashMap = new HashMap<Character, ArrayList<CustomerClient>>();
    static int iD = 1000;
    private HashMap<String, Integer> allBranchCount = new HashMap<String, Integer>();
    //lock object
    Object lock = new Object();

    private HashMap<String, Integer>serverPort = new HashMap<>();

    //setter for ORB
    public void setORB(ORB orb_val) {
        orb = orb_val;
    }


    public NBOpsImplementation() {
        serverPort.put("QC" , 5876 );
        serverPort.put("BC" , 6876 );
        serverPort.put("MB" , 7876 );
        serverPort.put("NB" , 8876 );
    }

    @Override
    public synchronized boolean createAccountRecord(String firstName, String lastName, String address, String phone, String branch) {


        int noErrorCount = 0;
        System.out.println("Inside createAccountRecord()...");
        //create ID
        String generateID = generateID(branch);
        //create a customer object
        CustomerClient customerClient = new CustomerClient(firstName, lastName, generateID, address, phone, branch);
        //customerClient.setBranch(branch);
        //To uppercase
        Character key = lastName.toUpperCase().charAt(0);
        //If key doesnt exist create one, and start the arraylist
        if (hashMap.containsKey(key)) {
            //get the arrayList if the key is present
            ArrayList<CustomerClient> temp = hashMap.get(key);
            temp.add(customerClient);
            hashMap.put(key, temp);
            noErrorCount++;

        } else {
            //Create a temp ArrayList for the value
            ArrayList<CustomerClient> temp = new ArrayList<CustomerClient>();
            temp.add(customerClient);
            //add to the map
            hashMap.put(key, temp);
            noErrorCount++;

        }
        // customerClient.toString();

        //print content of the arraylist for testing
        ArrayList<CustomerClient> temp = hashMap.get(key);
        for (CustomerClient cc : temp
                ) {
            System.out.println(cc.toString());
        }

        if (noErrorCount > 0) {
            //log stuff
            try {
                String msg = "Account created " + customerClient.getAccountNumber();
                generateLogFileServer(msg, "Server" , "NBServer");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        } else
            //log stuff
            try {
                String msg = "Account creation failed";
                generateLogFileServer(msg, "Server" , "NBServer");
            } catch (IOException e) {
                e.printStackTrace();
            }
        return false;

    }

    private String generateID(String branch) {


        String ID = branch + "C" + iD;
        System.out.println(ID);
        iD++;
        return ID;


    }

    @Override
    public synchronized boolean editRecord(String customerID, String fieldName, String newValue) {

        //Check if the field
        if (fieldName.equalsIgnoreCase("branch")) {
            if (newValue.equalsIgnoreCase("QC") || newValue.equalsIgnoreCase("BC") ||
                    newValue.equalsIgnoreCase("MB") || newValue.equalsIgnoreCase("NB")) {

                //Lookup customer from the hash map
                CustomerClient customerClient = varifiedEntryLookForUser(customerID);
                if (customerClient != null) {
                    customerClient.setBranch(newValue);
                    //log stuff
                    try {
                        String msg = "Field name change for " + customerID;
                        generateLogFileServer(msg, "Server" , "NBServer");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            } else {
                System.out.println("Invalid branch name. Please select from QC, BC, NB or MB");
                return false;
            }
        }
        //check if the user wants to change phone
        else if (fieldName.equalsIgnoreCase("phone")) {
            CustomerClient customerClient = varifiedEntryLookForUser(customerID);
            if (customerClient != null) {
                customerClient.setPhone(newValue);
                System.out.println("Updated phone number! of Client " + customerClient.getAccountNumber() +
                        " to " + customerClient.getPhone());
                //log stuff
                try {
                    String msg = "Field name change for " + customerID;
                    generateLogFileServer(msg, "Server" , "NBServer");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }

        }
        //check if the user wants to change address
        else if (fieldName.equalsIgnoreCase("address")) {
            CustomerClient customerClient = varifiedEntryLookForUser(customerID);
            if (customerClient != null) {
                customerClient.setAddress(newValue);
                //log stuff
                try {
                    String msg = "Field name change for " + customerID;
                    generateLogFileServer(msg, "Server" , "NBServer");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        } else {
            System.out.println("Invalid field name. Please select branch, address or phone!");
            return false;
        }

        System.out.println("User with " + customerID + " doesn't exist in the DB");

        return false;



    }

    private CustomerClient varifiedEntryLookForUser(String customerID) {


        // Getting a Set of Key-value pairs
        Set entrySet = hashMap.entrySet();

        // Obtaining an iterator for the entry set
        Iterator it = entrySet.iterator();

        // Iterate through HashMap entries(Key-Value pairs)
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();

            System.out.println("Key is: " + me.getKey());
            //iterate with the arraylist
            ArrayList<CustomerClient> temp = hashMap.get(me.getKey());
            for (CustomerClient cc : temp
                    ) {
                if (cc.getAccountNumber().equalsIgnoreCase(customerID)) {
                    System.out.println("Found user!");
                    return cc;
                }
            }

        }
        System.out.println("Customer does not exist in the DB");

        return null;
    }

    @Override
    public synchronized String[] getAccountCount() {


        allBranchCount.put("NB", hashArraylistSize());
        Thread t1 = new Thread(this);
        Thread t2 = new Thread(this);
        Thread t3 = new Thread(this);


        //thread names
        t1.setName("QC");
        t2.setName("MB");
        t3.setName("BC");

        t1.start();
        t2.start();
        t3.start();


        try {
            t1.join();
            t2.join();
            t3.join(); }
        catch (InterruptedException e) {
            e.printStackTrace();
        }


        String [] countOfCustomer = new String [allBranchCount.size()];

        int i = 0;
        for (String key : allBranchCount.keySet()) {
            countOfCustomer[i] = (key + " " + allBranchCount.get(key));
            i++;
        }

        System.out.println("-----------Printing Array of Strings---------------");

        for (String aCountOfCustomer : countOfCustomer) {
            System.out.println(aCountOfCustomer);
        }

        System.out.println("--------------------------");
        try {
            String msg = "Get account count";
            generateLogFileServer(msg, "Server" , "NBServer");
        } catch (IOException e) {
            e.printStackTrace();
        }



        return countOfCustomer;
    }

    public int hashArraylistSize() {


        int total = 0;


        // Getting a Set of Key-value pairs
        Set entrySet = hashMap.entrySet();

        // Obtaining an iterator for the entry set

        // Iterate through HashMap entries(Key-Value pairs)
        for (Object anEntrySet : entrySet) {
            Map.Entry me = (Map.Entry) anEntrySet;
            //System.out.println("Key is: "+ me.getKey());
            //iterate with the arraylist
            ArrayList<CustomerClient> temp = hashMap.get(me.getKey());
            total += temp.size();

        }
        return total;


    }
    @Override
    public void run() {
        Thread t = Thread.currentThread();
        String name = t.getName();
        System.out.println(name);
         /*
        qc->5876
        BC->6876
        MB->7876
        NB->8876

        */
        switch (name) {
            case "MB":
                serverCount(7876, "MB");
                break;
            case "BC":
                serverCount(6876, "BC");
                break;
            default:
                serverCount(5876, "QC");
                break;
        }

    }

    private void serverCount(int potNum, String branchName) {



        try {

            //create socket
            DatagramSocket clientSocket = new DatagramSocket();
            //Get ip address
            InetAddress IPAddress = InetAddress.getByName("localhost");
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            String sentence = "0 ";
            //convert the request to bytes
            sendData = sentence.getBytes();
            //send the stuff

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, potNum);
            clientSocket.send(sendPacket);


            //Receive the stuff
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            //Store it in a variable
            String receivedValue = new String(receivePacket.getData());
            //Print out the data
            System.out.println("FROM SERVER:" + receivedValue.trim());
            //Parse the data

            receivedValue=receivedValue.trim();
            System.out.println(receivedValue);

            synchronized (lock) {
                allBranchCount.put(branchName, Integer.valueOf(receivedValue));
            }

            clientSocket.close();

            System.out.println("Added count from " + branchName + " server");


        } catch (NumberFormatException | IOException ignored) { }

    }


    public synchronized boolean transferFund(String managerID, double amount, String sourceCustomerID, String destinationCustomerID) {
        //check if there's enough balance
        Boolean hasEnoughBalance = checkIfEnoughBalance(sourceCustomerID, amount);

        //if there's enough money send the money
        if(hasEnoughBalance){


            //bypass UDP if the same server
            String senderSever = sourceCustomerID.substring(0,2);
            String reciverSever = destinationCustomerID.substring(0,2);
            if (senderSever.equalsIgnoreCase(reciverSever)){
                boolean gotTheCash = deposit(destinationCustomerID, amount);
                if(gotTheCash) {
                    try {
                        String msg = "Manager " + managerID + "called transfer money for " + sourceCustomerID + "to " +
                                destinationCustomerID + " success";
                        generateLogFileServer(msg, "Server", "NBServer");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                else
                    return false;

            }







            Boolean sendMoney = sendMoney(destinationCustomerID, amount);

            if (sendMoney) {
                CustomerClient client = varifiedEntryLookForUser(sourceCustomerID);
                client.setBalance(client.getBalance() - amount);

                try {
                    String msg = "Manager " + managerID + "called transfer money for " + sourceCustomerID + "to " +
                            destinationCustomerID;
                    generateLogFileServer(msg, "Server", "NBServer");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //flatbush zombies
            return sendMoney;
        }
        else
            return false;
    }

    private Boolean sendMoney(String destinationCustomerID, double amount) {

        String serverName = destinationCustomerID.substring(0, 2).toUpperCase();

        try {

            //create socket
            DatagramSocket clientSocket = new DatagramSocket();
            //Get ip address
            InetAddress IPAddress = InetAddress.getByName("localhost");
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];
            String sentence = "1 " + destinationCustomerID + " " + amount;
            //convert the request to bytes
            sendData = sentence.getBytes();
            //send the stuff

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, serverPort.get(serverName));
            clientSocket.send(sendPacket);


            //Receive the stuff
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            //Store it in a variable
            String receivedValue = new String(receivePacket.getData());
            //Print out the data
            System.out.println("FROM SERVER:" + receivedValue.trim());
            //Parse the data

            receivedValue=receivedValue.trim();
            System.out.println(receivedValue);

            if(receivedValue.equalsIgnoreCase("Success")){
                clientSocket.close();
                return true;
            }
            else {
                clientSocket.close();
                return false;
            }



            //System.out.println("Added count from " + branchName + " server");


        } catch (NumberFormatException | IOException ignored) { }




        return false;
    }

    private Boolean checkIfEnoughBalance(String sourceCustomerID, double amount) {
        CustomerClient customerClient = varifiedEntryLookForUser(sourceCustomerID);
        return customerClient != null && amount < getBalance(customerClient.getAccountNumber());
    }

    @Override
    public synchronized boolean deposit(String customerID, double amt) {

        double total = 0;
        CustomerClient customerClient = varifiedEntryLookForUser(customerID);
        if(customerClient != null){
            total = customerClient.getBalance();
            total += amt;
            customerClient.setBalance(total);
            System.out.println("Money deposited. Customer " + customerID + " has $" + total + " in the account");

            //log stuff
            try {
                String msg = "Deposit made for " + customerID;
                generateLogFileServer(msg, "Server" , "NBServer");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        else{
            System.out.println("Customer " + customerID + " doesn't exist in the database!");
            ///log stuff
            try {
                String msg = "Deposit failed " + customerID;
                generateLogFileServer(msg, "Server" , "NBServer");
            } catch (IOException e) {
                e.printStackTrace();
            }


            return false;
        }
    }

    @Override
    public synchronized boolean withdraw(String customerID, double amt) {

        double total = 0;
        CustomerClient customerClient = varifiedEntryLookForUser(customerID);
        if(customerClient != null){
            total = customerClient.getBalance();
            total -= amt;
            customerClient.setBalance(total);
            System.out.println("Money withdrawn. Customer " + customerID + " has $" + total + " in the account");
            //log stuff
            try {
                String msg = "Withdraw  " + customerID;
                generateLogFileServer(msg, "Server" , "NBServer");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        else{
            System.out.println("Customer " + customerID + " doesn't exist in the database!");
            //log stuff
            try {
                String msg = "Customer doesnt exist " + customerID;
                generateLogFileServer(msg, "Server" , "NBServer");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

    }

    @Override
    public synchronized double getBalance(String customerID) {

        double total = 0;
        CustomerClient customerClient = varifiedEntryLookForUser(customerID);
        if(customerClient != null){
            total = customerClient.getBalance();
            System.out.println("Customer " + customerID + " has $" + total + " in the account");
            //log stuff
            try {
                String msg = "Get balance for " + customerID;
                generateLogFileServer(msg, "Server" , "NBServer");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return total;
        }
        else{
            System.out.println("Customer " + customerID + " doesn't exist in the database!");
            return 0;
        }

    }


}

