package com.mongodb.prudential.load;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.prudential.LoadGenData;
import com.mongodb.prudential.connect.MongoClientConnection;


public class FindCustomer {
	
	//private MongoClient mongoClient = null;
	private MongoDatabase prudentialDB = null;
	

	public FindCustomer()
	{
	
		connectMongoClient();
	}

	private void connectMongoClient()
	{
		MongoClientConnection mongoConnector = MongoClientConnection.getInstance();
		//String ConnectionStr = "mongodb://"+mongodbUser+":"+mongodbPass+"@"+mongodbEndpoint;
		//String ConnectionStr = "mongodb+srv://"+mongodbUser+":"+mongodbPass+"@"+mongodbEndpoint;
		//System.out.println ("InsertCustomer.connectMongoClient:"); 
		try{
			
			
			if (prudentialDB == null)
				prudentialDB = mongoConnector.getMongoDatabase();
				
		}catch(Exception e)
	    {
	    	System.out.println("Making MongoDB Connection Exception:"+e.toString());
	    }
	}
	
	
	public void findCustomerData(int accountNo) {
	    try{
	    	connectMongoClient();
	        MongoCollection<Document> customerCollection = prudentialDB.getCollection(LoadGenData.getCollection());
	        
	        findOneDocument(customerCollection, accountNo);
	        
	    }catch(Exception e)
	    {
	    	if (LoadGenData.isPrintLog()) System.out.println("Fail Find Document : "+accountNo);
	    	System.out.println("During Find - Exception: "+e.toString());
	    }
	}
	
	private void findOneDocument(MongoCollection<Document> customerCollection, int accountNo) throws Exception{
		
		Bson filter = eq("customer_id", accountNo);
		
		
		List<Document> iterDoc = customerCollection.find(filter).projection(fields(excludeId(),include("customer_id","person_group_code"))).into(new ArrayList<Document>());
		//System.out.println(""+iterDoc.first().size());
		for (Document customer : iterDoc) {
			if (LoadGenData.isPrintLog()) System.out.println("Success Find Document : "+customer.toJson());
            //System.out.println("Success Find:" + customer.toJson());
        }
    }
}
