package com.mongodb.prudential.load;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoSocketReadTimeoutException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.prudential.LoadGenData;
import com.mongodb.prudential.connect.MongoClientConnection;

import static com.mongodb.client.model.Updates.*;

public class InsertCustomer {
	
	//private MongoClientConnection = null;
	private MongoDatabase prudentialDB = null;
	
	
	public InsertCustomer()
	{
		//System.out.println ("InsertCustomer.InsertCustomer:"); 
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
	
	
	public void insertCustomerData(int accountNo) {
	    try{
	    	connectMongoClient();
	    	//System.out.println ("InsertCustomer.insertCustomerData:"); 
	        MongoCollection<Document> customerCollection = prudentialDB.getCollection(LoadGenData.getCollection());
	        
	        insertOneDocument(customerCollection, accountNo);
	        if (LoadGenData.isPrintLog()) System.out.println("Success Insert Document : "+accountNo);
	    }catch(MongoSocketReadTimeoutException e)
	    {
	    	if (LoadGenData.isPrintLog()) System.out.println("Timeout Insert Document : "+accountNo);
	    }
	    catch(Exception e)
	    {
	    	if (LoadGenData.isPrintLog()) System.out.println("Fail Insert Document : "+accountNo);
	    	System.out.println("During Insert - Exception: "+e.toString());
	    }
	    //closeMongoClient();
	}
	
	private void insertOneDocument(MongoCollection<Document> customerCollection, int accountNo) throws Exception{
		//System.out.println ("InsertCustomer.insertOneDocument:"); 
		Document customerDoc = generateNewCustomer(accountNo);
		//System.out.println ("Finish InsertCustomer.generateNewCustomer:"); 
		customerCollection.insertOne(customerDoc);
        //System.out.println("One grade inserted for studentId 10000.");
    }
	
	private Document generateNewCustomer(int accountNo) throws Exception{
		
		List<Document> address = new ArrayList<Document> ();
		address.add(new Document("address_type", "Office").append("address_id","서울시 강남구 삼성동").append("address_detail", "코엑스 6"));
		address.add(new Document("address_type", "Home").append("address_id","서울시 강남구 역삼동").append("address_detail", "역삼 한국 아파트 101동 101호"));
		
		
		List<Document> group_customer_relation = new ArrayList<Document> ();
		group_customer_relation.add(new Document("group_relation_type", "A").append("group_relation_fact","G0001").append("group_relation_co", "단체회사").append("contact", "02-555-5555"));
		group_customer_relation.add(new Document("group_relation_type", "B").append("group_relation_fact","G0002").append("group_relation_co", "그룹회사").append("contact", "02-555-7777"));
		
		List<Document> u_customer_identity = new ArrayList<Document> ();
		u_customer_identity.add(new Document("identity_code", "식별항목1").append("identity_value","식별값A"));
		u_customer_identity.add(new Document("identity_code", "식별항목2").append("identity_value","식별값B"));
		
	    List<String> job_code = new ArrayList<String>();
	    job_code.add("사무직");
	    job_code.add("내근직");
	    
	    List<String> corp_customer_no = new ArrayList<String>();
	    corp_customer_no.add("0000");
	    corp_customer_no.add("0001");
	    
	    Document newRow = null;
	    
	    Document tempcustomer = new Document("temp_customer_type", "A")
				.append("customer_english_name","Gildong")
                .append("birthdate", new SimpleDateFormat("yyyy-MM-dd").parse("1995-01-10"))
                .append("gender", "M");
	    
		Document customerBasic = new Document("customer_english_name", "Gildong Hong "+accountNo)
				.append("demostic_type","Y")
				.append("gender_code", "M")
                .append("birthdate", new SimpleDateFormat("yyyy-MM-dd").parse("1995-01-10"))
                .append("nation", "KR")
                .append("job_code",job_code)
                .append("drive", "Y")
                .append("social_security_number", "950110-123456")
                .append("passport", "KR090001")
                .append("driver_license", "서울10001")
                .append("tempcustomer", tempcustomer)
                .append("corp_customer_no", corp_customer_no);
		
		
		newRow = new Document("customer_id", accountNo)
				.append("person_group_code", "A")
				.append("customer_name", "홍길동" + accountNo)
                .append("address", address)
                .append("group_customer_relation", group_customer_relation)
                .append("u_customer_identity", u_customer_identity)
                .append("customerBasic", customerBasic);

        return newRow;
    }
	

	
	private static String isNull(String input)
	{
		return (input.equals("") || input.isEmpty())? "" : input ;
	}


}
