package Servers;

import BankingOperationsApp.BankingOperations;
import BankingOperationsApp.BankingOperationsHelper;
import BankingOpsImplementation.BCOpsImplementation;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class BCServer{

    private BCOpsImplementation bcOpsImplementation = new BCOpsImplementation();


        /*
        qc->5876
        BC->6876
        MB->7876
        NB->8876

       */

    public static void main(String args[]) {

        BCServer bcServer = new BCServer();
        Thread t1 = new Thread(() -> bcServer.startCorba());
        Thread t2 = new Thread(() -> bcServer.startUDP());

        t1.start();
        t2.start();


        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void startUDP() {



        DatagramSocket serverSocket = null;
        try {
            serverSocket = new DatagramSocket(6876);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        String tranferConfirmation;
        System.out.println("BC Server's UDP ready and waiting ...");


        while (true) {

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                assert serverSocket != null;
                serverSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String sentence = new String(receivePacket.getData());
            sentence = sentence.trim();
            System.out.println("RECEIVED: " + sentence);
            String[] result = sentence.split("\\s");

            for (String aResult : result) System.out.println(aResult);

            int switchTo = Integer.parseInt(result[0]);

            InetAddress IPAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();



            //switch cases for different work
            switch (switchTo){

                case 0:
                    int numOfClient = bcOpsImplementation.hashArraylistSize();
                    String accountCount = String.valueOf(numOfClient);
                    //convert it to Byte
                    sendData = accountCount.getBytes();
                    break;
                case 1:


                    boolean isTransferred = bcOpsImplementation.deposit(result[1], Double.parseDouble(result[2]));
                    if(isTransferred) {
                        tranferConfirmation = "Success";
                        sendData = tranferConfirmation.getBytes();
                    }
                    else {
                        tranferConfirmation = "Not";
                        sendData = tranferConfirmation.getBytes();
                    }

                    break;
            }



            //Send the connection
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, port);
            //send the data...
            try {
                serverSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    private void startCorba() {

        String [] args = {"-ORBInitialPort", "1050" , "-ORBInitialHost",  "localhost"};

        try{
            // create and initialize the ORB //// get reference to rootpoa &amp; activate the POAManager
            ORB orb = ORB.init(args, null);

            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            bcOpsImplementation.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(bcOpsImplementation);

            //Get the stub from AdditionHelper
            BankingOperations href = BankingOperationsHelper.narrow(ref);

            //  implementation repository stuff
            // activates the server with the name provided below
            org.omg.CORBA.Object objRef =  orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent path[] = ncRef.to_name( "BC" );
            ncRef.rebind(path, href);

            System.out.println("BC Server ready and waiting ...");

            // wait for invocations from clients
            while (true) orb.run();
        }

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("HelloServer Exiting ...");
    }


}


