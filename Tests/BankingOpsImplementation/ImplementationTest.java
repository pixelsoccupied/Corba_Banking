package BankingOpsImplementation;

import org.junit.*;
import org.omg.CORBA.ORB;

import static org.junit.Assert.*;

public class ImplementationTest{


    @Test
    public  void createAccountRecord() throws Exception {
        //ORB orb = new ORB();
       // bcOpsImplementation.setORB();
        BCOpsImplementation bcOpsImplementation = new BCOpsImplementation();

        Boolean shouldBeTrue = bcOpsImplementation.createAccountRecord("Fname", "lname" , "Elm street","992", "BC"  );
        Boolean shouldBeTrue2 = bcOpsImplementation.createAccountRecord("Fname2", "lname2" , "Elm street2","9922", "BC"  );

        assertEquals(true , shouldBeTrue);
        assertEquals(true , shouldBeTrue2);

    }

    @Test
    public void editRecord() throws Exception {
        BCOpsImplementation bcOpsImplementation = new BCOpsImplementation();
        Boolean bla = bcOpsImplementation.createAccountRecord("Fname", "lname" , "Elm street","992", "BC"  );
        Boolean shouldBeTrue =  bcOpsImplementation.editRecord("BCC1000", "phone", "991");
        assertEquals(true, shouldBeTrue);

    }

    @Test
    public void getAccountCount() throws Exception {
        BCOpsImplementation bcOpsImplementation = new BCOpsImplementation();

        String[] count = bcOpsImplementation.getAccountCount();
        assertNotNull(count);

    }

    @Test
    public void deposit() throws Exception {
        BCOpsImplementation bcOpsImplementation = new BCOpsImplementation();
        Boolean bla = bcOpsImplementation.createAccountRecord("Fname", "lname" , "Elm street","992", "BC"  );
        Boolean shouldBeTrue =  bcOpsImplementation.deposit( "BCC1000", 1000);
        assertEquals(true, shouldBeTrue);
    }


    @Test
    public void withdraw() throws Exception {
        BCOpsImplementation bcOpsImplementation = new BCOpsImplementation();
        Boolean bla = bcOpsImplementation.createAccountRecord("Fname", "lname" , "Elm street","992", "BC"  );
        Boolean dep =  bcOpsImplementation.deposit( "BCC1000", 1000);
        Boolean shouldBeTrue =  bcOpsImplementation.withdraw( "BCC1000", 400);
        assertEquals(true, shouldBeTrue);
    }


    @Test
    public void transferFund() throws Exception {
        BCOpsImplementation bcOpsImplementation = new BCOpsImplementation();
        QCOpsImplementation qcOpsImplementation = new QCOpsImplementation();

        Boolean bla = bcOpsImplementation.createAccountRecord("Fname", "lname" , "Elm street","992", "BC"  );
        Boolean bla2 = bcOpsImplementation.createAccountRecord("Fname2", "lname2" , "Elm street2","9922", "BC"  );

        //TODD:UDP between servers
        //Boolean qcbla2 = qcOpsImplementation.createAccountRecord("Fname2", "lname2" , "Elm street2","9922", "QC"  );


        Boolean dep =  bcOpsImplementation.deposit( "BCC1000", 1000);


        Boolean shouldBeTrue =  bcOpsImplementation.transferFund("Auto", 100, "BCC1000", "BCC1001");
        //Boolean shouldBeTrueQC =  bcOpsImplementation.transferFund("Auto", 100, "BCC1000", "QCC1000");

        assertEquals(true,shouldBeTrue);
        //assertEquals(true,shouldBeTrueQC);


    }

    @Test
    public void getBalance() throws Exception {
        BCOpsImplementation bcOpsImplementation = new BCOpsImplementation();
        Boolean bla = bcOpsImplementation.createAccountRecord("Fname", "lname" , "Elm street","992", "BC"  );
        Boolean dep =  bcOpsImplementation.deposit( "BCC1000", 1000);
        double balance = bcOpsImplementation.getBalance("BCC1000");
        assertEquals(2600, balance, 0.01);

    }

}