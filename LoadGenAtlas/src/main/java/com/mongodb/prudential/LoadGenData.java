package com.mongodb.prudential;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.mongodb.prudential.load.DeleteCustomer;
import com.mongodb.prudential.load.FindCustomer;
import com.mongodb.prudential.load.InsertCustomer;
import com.mongodb.prudential.load.UpdateCustomer;
import com.mongodb.prudential.load.UpdateManyCustomer;


public class LoadGenData {
	
	private static String mongodbConnection = "";
	private static boolean printLog = false;
	private static String collection = "";
	private static String database = "";
	public static String getDatabase() {
		return database;
	}

	public static void setDatabase(String database) {
		LoadGenData.database = database;
	}

	private static String updatedata = "";
			
	
	public static String getUpdatedata() {
		return updatedata;
	}

	public static void setUpdatedata(String updatedata) {
		LoadGenData.updatedata = updatedata;
	}

	public static String getCollection() {
		return collection;
	}

	public static void setCollection(String collection) {
		LoadGenData.collection = collection;
	}

	public static String getMongodbConnection() {
		return mongodbConnection;
	}

	public static boolean isPrintLog() {
		return printLog;
	}

	public static void setPrintLog(boolean printLog) {
		LoadGenData.printLog = printLog;
	}

	public static void setMongodbConnection(String mongodbConnection) {
		LoadGenData.mongodbConnection = mongodbConnection;
	}

	public LoadGenData(String mongodbEndpoint, String mongodbUser, String mongodbPass, String mongodriver)
	{
		super();
		LoadGenData.setMongodbConnection (mongodriver + mongodbUser+ ":"+mongodbPass + "@"+mongodbEndpoint);	
	}
	
	public void commandRunning(String dbcommand, String prefix, int startX, int endX, int sleepmi, int findloop, int threadcount)
	{
		InsertCustomer insert = null;
		UpdateCustomer update = null;
		DeleteCustomer delete = null;
		UpdateManyCustomer updatemany=null;
		FindCustomer find = null;
		
		if (dbcommand.equals("I"))
		{
			System.out.println ("Insertion starts *****************"); 
			/*
			insert = new InsertCustomer();
    		try {
	    		for (int startIdx = startX; startIdx <= endX; startIdx++)
				{
	    			insert = new InsertCustomer();
	    			insert.insertCustomerData(startIdx);
	    			Thread.sleep(sleepmi);
				}
    		}catch(Exception e)
			{
				System.out.println("During Insertion Java Thread error : Thread.sleep"+e.toString());
			}
			*/
			for (int i=0;i<threadcount;i++)
			{
				InsertThreadRunner insertTreadrunner = new InsertThreadRunner(i, startX, endX, sleepmi);
				insertTreadrunner.start();
			}
		}
		else if (dbcommand.equals("M"))
		{
			System.out.println ("Update Many starts *****************"); 
			updatemany = new UpdateManyCustomer();
    		try {
    			updatemany.updateManyCustomerData(endX);
    		}catch(Exception e)
			{
				System.out.println("During Insertion Java Thread error : Thread.sleep"+e.toString());
			}
			//for (int i=0;i<threadcount;i++)
			//{
			//	InsertThreadRunner insertTreadrunner = new InsertThreadRunner(i, startX, endX, sleepmi);
			//	insertTreadrunner.start();
			//}
		}
		else if(dbcommand.equals("U"))
		{
			System.out.println ("Update starts *****************"); 
			update = new UpdateCustomer();
			try {
	    		for (int startIdx = startX; startIdx <= endX; startIdx++)
				{
	    			//update = new UpdateCustomer();
	    			update.updateCustomerData(startIdx);
					Thread.sleep(sleepmi);
				}
    		}catch(Exception e)
			{
				System.out.println("During Update Java Thread error : Thread.sleep"+e.toString());
			}
		}
		else if (dbcommand.equals("D"))
		{
			//delete = new DeleteCustomer(mongodbEndpoint, mongodbUser, mongodbPass);
    		//delete.genDeleteCustomerData(prefix, startX, endX, sleepmi);
			System.out.println ("Delete starts *****************"); 
			delete = new DeleteCustomer();
			try {
	    		for (int startIdx = startX; startIdx <= endX; startIdx++)
				{
	    			//delete = new DeleteCustomer();
	    			delete.deleteCustomerData(startIdx);
					Thread.sleep(sleepmi);
				}
    		}catch(Exception e)
			{
				System.out.println("During delete Java Thread error : Thread.sleep"+e.toString());
			}
		}
		else if (dbcommand.equals("F"))
		{
			//delete = new DeleteCustomer(mongodbEndpoint, mongodbUser, mongodbPass);
    		//delete.genDeleteCustomerData(prefix, startX, endX, sleepmi);
			//System.out.println ("Find starts  *****************"); 
			//find = new FindCustomer();
			//try {
			//	int i=0;
			//	while (i < findloop)
			//	{
		   // 		for (int startIdx = startX; startIdx <= endX; startIdx++)
			//		{
		    //			find.findCustomerData(startIdx);
			//			Thread.sleep(sleepmi);
			//		}
		    //		i++;
			//	}
    		//}catch(Exception e)
			//{
			//	System.out.println("Java Thread error : Thread.sleep"+e.toString());
			///}
			for (int i=0;i<threadcount;i++)
			{
				ThreadRunner findTreadrunner = new ThreadRunner(i, startX, endX, sleepmi, findloop);
				findTreadrunner.start();
			}
		}
		else
		{
			System.out.println ("The operation command have to be one in the options [I, U, D, F]");
    	}
		
	}

    public static void main(String[] args) throws Exception {
    	String dbcommand = null;
    	String prefix = null;
    	int startX = 0;
    	int endX = 0;
    	
    	int findloop=0;
    	int threadcount=0;
    	
    	String mongodbEndpoint = null;
    	String mongodbUser = null;     	
    	String mongodbPass = null;
    	String mongodbdriver  = null;
    	String collectionName = null;
    	String databaseName=null;
    	
    	int sleepMiSec=0;
    	
    	LoadGenData load = null;
    			
    	try {
    		
    		InputStream inputStream = new FileInputStream(args[0]);
            Properties prop = new Properties();
            
         // load a properties file
        	prop.load(inputStream);
        	
        	// get value by key
        	dbcommand = prop.getProperty("mongodb.command");
        	startX = (new Integer(prop.getProperty("mongodb.start"))).intValue();
        	endX = (new Integer(prop.getProperty("mongodb.end"))).intValue();

        	mongodbEndpoint = prop.getProperty("mongodb.endpoint");
        	mongodbUser = prop.getProperty("mongodb.user");
        	mongodbPass = prop.getProperty("mongodb.pass");
        	mongodbdriver = prop.getProperty("mongodb.driver");
        	collectionName = prop.getProperty("mongodb.collection");
        	databaseName = prop.getProperty("mongodb.database");
        	updatedata = prop.getProperty("mongodb.updatedata");
        	
        	sleepMiSec = (new Integer(prop.getProperty("sleepmi"))).intValue();
        	findloop = (new Integer(prop.getProperty("findloop"))).intValue();
        	threadcount = (new Integer(prop.getProperty("threadcount"))).intValue();
        	
        	setPrintLog(new Boolean(prop.getProperty("mongodb.print")).booleanValue() );
        	setCollection(collectionName);
        	setDatabase(databaseName);
        	
        	setUpdatedata(updatedata);
        	
        	
        	
        	load = new LoadGenData(mongodbEndpoint,mongodbUser, mongodbPass, mongodbdriver);
        	load.commandRunning(dbcommand, prefix, startX, endX, sleepMiSec, findloop,threadcount);
        } catch (NumberFormatException ex) {
        	System.out.println("Start and End must be number format");
        	System.out.println(ex.toString());
        }
    	catch (IOException io) {
            io.printStackTrace();
        }
    	
    	

    }
}