package com.mongodb.prudential.load;


import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.prudential.LoadGenData;
import com.mongodb.prudential.connect.MongoClientConnection;

public class DeleteCustomer {
	//private MongoClient mongoClient = null;
	private MongoDatabase prudentialDB = null;
	
	public DeleteCustomer()
	{
		connectMongoClient();
	}
	

	private void connectMongoClient()
	{
		//String ConnectionStr = "mongodb://"+mongodbUser+":"+mongodbPass+"@"+mongodbEndpoint;
		//String ConnectionStr = "mongodb+srv://"+mongodbUser+":"+mongodbPass+"@"+mongodbEndpoint;
		try{
			MongoClientConnection mongoConnector = MongoClientConnection.getInstance();
			
			
			if (prudentialDB == null)
				prudentialDB = mongoConnector.getMongoDatabase();
				
		}catch(Exception e)
	    {
	    	System.out.println("Making MongoDB Connection Exception:"+e.toString());
	    }
	}
	
	
	public void deleteCustomerData(int accountNo) {
	    try{
	    	connectMongoClient();
	        MongoCollection<Document> customerCollection = prudentialDB.getCollection(LoadGenData.getCollection());
	        
	        deleteOneDocument(customerCollection, accountNo);
	        if (LoadGenData.isPrintLog()) System.out.println("Success Delete Document : "+accountNo);
	    }catch(Exception e)
	    {
	    	if (LoadGenData.isPrintLog()) System.out.println("Fail Delete Document : "+accountNo);
	    	System.out.println("During Delete - Exception: "+e.toString());
	    	MongoClientConnection mongoConnector = MongoClientConnection.getInstance();
	    	mongoConnector.init();
	    }
	}
	
	private void deleteOneDocument(MongoCollection<Document> customerCollection, int accountNo) throws Exception{
		
		Bson filter = eq("customer_id", accountNo);
		
		customerCollection.deleteOne(filter);
		//System.out.println("One grade delete for studentId 10000.");
    }
}
