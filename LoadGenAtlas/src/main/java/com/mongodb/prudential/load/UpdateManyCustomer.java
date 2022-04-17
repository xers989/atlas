package com.mongodb.prudential.load;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lte;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.mongodb.prudential.LoadGenData;
import com.mongodb.prudential.connect.MongoClientConnection;

public class UpdateManyCustomer {
	
//private MongoClient mongoClient = null;
private MongoDatabase prudentialDB = null;


public UpdateManyCustomer()
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


public void updateManyCustomerData(int accountNo) {
    try{
    	connectMongoClient();
        MongoCollection<Document> customerCollection = prudentialDB.getCollection(LoadGenData.getCollection());
       
        updateManyDocument(customerCollection, accountNo);
        if (LoadGenData.isPrintLog()) System.out.println("Success Update Document : "+accountNo);
    }catch(Exception e)
    {
    	if (LoadGenData.isPrintLog()) System.out.println("Fail Update Document : "+accountNo);
    	System.out.println("During Update - Exception: "+e.toString());
    	MongoClientConnection mongoConnector = MongoClientConnection.getInstance();
    	mongoConnector.init();
    }
}

private void updateManyDocument(MongoCollection<Document> customerCollection, int accountNo) throws Exception{
	
	//Bson filter = eq("u_customer_no", accountNo);
	Bson filter = lte("customer_id", accountNo);
	
	Bson customerDoc = generateUpdateCustomer(accountNo);
	customerCollection.updateMany(filter, customerDoc);
	//customerCollection.updateOne(filter, customerDoc);
    //System.out.println("One grade updated for studentId 10000.");
}

private Bson generateUpdateCustomer(int accountNo) throws Exception{
	
	
	Document address = new Document("address_type", "Home2").append("address_id","서울시 영등포구 여의도동").append("address_detail", "여의도 아파트 101동 101호");
	    
    
    Bson update1 = set("person_group_code", "AA"); // increment x by 10. As x doesn't exist yet, x=10.
	Bson update2 = push("job_code", "별정직"); // rename variable "class_id" in "new_class_id".]
	Bson update3 = push("address", address);
	Bson update4 = set("update_field", LoadGenData.getUpdatedata());
	

    return Updates.combine( update1, update2, update3, update4);
}

private static String isNull(String input)
{
	return (input.equals("") || input.isEmpty())? "" : input ;
}
}