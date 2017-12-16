package BankingOperationsApp;


/**
* BankingOperationsApp/BankingOperationsPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from BankingOperations
* Wednesday, October 25, 2017 4:14:15 o'clock PM EDT
*/

public abstract class BankingOperationsPOA extends org.omg.PortableServer.Servant
 implements BankingOperationsApp.BankingOperationsOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("createAccountRecord", new java.lang.Integer (0));
    _methods.put ("editRecord", new java.lang.Integer (1));
    _methods.put ("getAccountCount", new java.lang.Integer (2));
    _methods.put ("transferFund", new java.lang.Integer (3));
    _methods.put ("deposit", new java.lang.Integer (4));
    _methods.put ("withdraw", new java.lang.Integer (5));
    _methods.put ("getBalance", new java.lang.Integer (6));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // BankingOperationsApp/BankingOperations/createAccountRecord
       {
         String firstName = in.read_string ();
         String lastName = in.read_string ();
         String address = in.read_string ();
         String phone = in.read_string ();
         String branch = in.read_string ();
         boolean $result = false;
         $result = this.createAccountRecord (firstName, lastName, address, phone, branch);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 1:  // BankingOperationsApp/BankingOperations/editRecord
       {
         String customerID = in.read_string ();
         String fieldName = in.read_string ();
         String newValue = in.read_string ();
         boolean $result = false;
         $result = this.editRecord (customerID, fieldName, newValue);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 2:  // BankingOperationsApp/BankingOperations/getAccountCount
       {
         String $result[] = null;
         $result = this.getAccountCount ();
         out = $rh.createReply();
         BankingOperationsApp.custCountHelper.write (out, $result);
         break;
       }


  //new method
       case 3:  // BankingOperationsApp/BankingOperations/transferFund
       {
         String managerID = in.read_string ();
         double amount = in.read_double ();
         String sourceCustomerID = in.read_string ();
         String destinationCustomerID = in.read_string ();
         boolean $result = false;
         $result = this.transferFund (managerID, amount, sourceCustomerID, destinationCustomerID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }


  //Customer specific methods
       case 4:  // BankingOperationsApp/BankingOperations/deposit
       {
         String customerID = in.read_string ();
         double amt = in.read_double ();
         boolean $result = false;
         $result = this.deposit (customerID, amt);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 5:  // BankingOperationsApp/BankingOperations/withdraw
       {
         String customerID = in.read_string ();
         double amt = in.read_double ();
         boolean $result = false;
         $result = this.withdraw (customerID, amt);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 6:  // BankingOperationsApp/BankingOperations/getBalance
       {
         String customerID = in.read_string ();
         double $result = (double)0;
         $result = this.getBalance (customerID);
         out = $rh.createReply();
         out.write_double ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:BankingOperationsApp/BankingOperations:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public BankingOperations _this() 
  {
    return BankingOperationsHelper.narrow(
    super._this_object());
  }

  public BankingOperations _this(org.omg.CORBA.ORB orb) 
  {
    return BankingOperationsHelper.narrow(
    super._this_object(orb));
  }


} // class BankingOperationsPOA
