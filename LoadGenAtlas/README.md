### LoadGenAtlas
Atlas MongoDB Load generator

#### Usage

Command.properties

Edit command.properties

`````
# Generation Command (I)nsert, (U)pdate, (D)elete, (F)ind
# The command will be occurred with index (start ~ end)
mongodb.command =I
mongodb.start = 1
mongodb.end = 10000
# connection information
mongodb.endpoint=shardedcluster.5qjlg.mongodb.net/myFirstDatabase?retryWrites=true&w=majority
mongodb.user=mongodb_user
mongodb.pass=Password00
mongodb.driver=mongodb+srv://
sleepmi=0
mongodb.print=true
mongodb.database=loadtestDB
mongodb.collection=customerload

# Find Option
# Find loop is 1, thread Count is #
findloop=1
threadcount=10

# Update Option
mongodb.updatedata=AAAA
`````

생성 되는 Json 문서는 다음과 같습니다.
`````
{ _id: ObjectId("625c1b60ba45de0f827b678f"),
  customer_id: 1,
  person_group_code: 'AA',
  customer_name: '홍길동1',
  address: 
   [ { address_type: 'Office',
       address_id: '서울시 강남구 삼성동',
       address_detail: '코엑스 6' },
     { address_type: 'Home',
       address_id: '서울시 강남구 역삼동',
       address_detail: '역삼 한국 아파트 101동 101호' },
     { address_type: 'Home2',
       address_id: '서울시 영등포구 여의도동',
       address_detail: '여의도 아파트 101동 101호' } ],
  group_customer_relation: 
   [ { group_relation_type: 'A',
       group_relation_fact: 'G0001',
       group_relation_co: '단체회사',
       contact: '02-555-5555' },
     { group_relation_type: 'B',
       group_relation_fact: 'G0002',
       group_relation_co: '그룹회사',
       contact: '02-555-7777' } ],
  u_customer_identity: 
   [ { identity_code: '식별항목1', identity_value: '식별값A' },
     { identity_code: '식별항목2', identity_value: '식별값B' } ],
  customerBasic: 
   { customer_english_name: 'Gildong Hong 1',
     demostic_type: 'Y',
     gender_code: 'M',
     birthdate: 1995-01-09T15:00:00.000Z,
     nation: 'KR',
     job_code: [ '사무직', '내근직' ],
     drive: 'Y',
     social_security_number: '950110-123456',
     passport: 'KR090001',
     driver_license: '서울10001',
     tempcustomer: 
      { temp_customer_type: 'A',
        customer_english_name: 'Gildong',
        birthdate: 1995-01-09T15:00:00.000Z,
        gender: 'M' },
     corp_customer_no: [ '0000', '0001' ] },
  job_code: [ '별정직' ],
  update_field: 'AAAA' }
`````
문서는 InsertCustomer.java 의 90 라인에 있으며 이를 customer_id를 유지하고 수정 할 수 있습니다. (Update, Delete, Find 에 사용 됨)

#### Insert Test
Command.properties 를 다음과 같이 편집 합니다.

`````
mongodb.command =I
mongodb.start = 1
mongodb.end = 100
mongodb.endpoint=shardedcluster.5qjlg.mongodb.net/myFirstDatabase?retryWrites=true&w=majority
mongodb.user=user_id
mongodb.pass=password00
mongodb.driver=mongodb+srv://
sleepmi=0
mongodb.print=true
mongodb.collection=shardCustomer
mongodb.database=shardDB

# Find Option
findloop=1

# thread option (find 만 적용)
threadcount=10

# Update Option
mongodb.updatedata=AAAA
`````
Command 를 I 로 하고 customer_id 가 1 ~ 100 으로 하여 생성 합니다. pritnt는 true/false 로 설정 할 수 있으며 log 내용을 추가 출력 하거나 하지 않는 설정 입니다.

Insert 는 single thread 로 동작하며 LoadGenData.java 의 78 라인을 다음 과 같이 수정 하면 multi thread 로 동작 합니다.
`````
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
`````
Multi Thread로 동작하는 경우 데이터 중복을 방지 하기 위해 customer_id 가 첫번째 Thread는 100001 ~ 100100, 두번째 Thread는 200001 ~ 200100 으로 생성 됩니다.


#### 실행 결과
컴파일 후 jar 파일을 생성 하여 다음과 같이 실행 합니다.

`````
% java -jar target/MongoDB-samples-1.0.0-jar-with-dependencies.jar command.properties 
Insertion starts *****************
Success Insert Document : 1
Success Insert Document : 2
Success Insert Document : 3
...
Success Insert Document : 99
Success Insert Document : 100
`````

컴렉션 정보를 확인 하면 데이터가 들어간 것을 확인 할 수 있습니다.

<img src="/LoadGenAtlas/images/images01.png" width="70%" height="70%">    

#### Find Test

검색을 위해서 다음과 같이 파일을 수정 합니다.

`````
mongodb.command =F
mongodb.start = 1
mongodb.end = 100
mongodb.endpoint=shardedcluster.5qjlg.mongodb.net/myFirstDatabase?retryWrites=true&w=majority
mongodb.user=user_id
mongodb.pass=password00
mongodb.driver=mongodb+srv://
sleepmi=0
mongodb.print=true
mongodb.collection=shardCustomer
mongodb.database=shardDB

# Find Option
findloop=1

# thread option (find 만 적용)
threadcount=10

# Update Option
mongodb.updatedata=AAAA
`````

Command 를 F 로 수정 하고 실행 합니다. 검색은 customer_id 에 대해서 1 ~ 100 까지 진행 합니다.
Multi Thread 가 10 으로 되어 있음으로 10 개의 스레드가 생성되며 각각 동작 합니다. (1~100 까지 각자 검색)
findloop 는 검색을 반복하는 횟수로 1로 설정 되어 있음으로 1~ 100 까지 1번 진행 합니다. 


#### 실행 결과
컴파일 후 jar 파일을 생성 하여 다음과 같이 실행 합니다.

`````
% java -jar target/MongoDB-samples-1.0.0-jar-with-dependencies.jar command.properties 
Find Thread #0 start
Find Thread #2 start
Find Thread #3 start
Find Thread #1 start
Find Thread #4 start
Find Thread #5 start
Find Thread #6 start
Find Thread #7 start
Find Thread #8 start
Find Thread #9 start
Success Find Document : {"customer_id": 1, "person_group_code": "A"}
Success Find Document : {"customer_id": 1, "person_group_code": "A"}
Success Find Document : {"customer_id": 1, "person_group_code": "A"}
Success Find Document : {"customer_id": 1, "person_group_code": "A"}
Success Find Document : {"customer_id": 1, "person_group_code": "A"}
Success Find Document : {"customer_id": 1, "person_group_code": "A"}
Success Find Document : {"customer_id": 1, "person_group_code": "A"}
Success Find Document : {"customer_id": 1, "person_group_code": "A"}
...
Find Thread #6 end
Success Find Document : {"customer_id": 98, "person_group_code": "A"}
Success Find Document : {"customer_id": 97, "person_group_code": "A"}
Success Find Document : {"customer_id": 99, "person_group_code": "A"}
Success Find Document : {"customer_id": 100, "person_group_code": "A"}
Success Find Document : {"customer_id": 98, "person_group_code": "A"}
Find Thread #4 end
Success Find Document : {"customer_id": 99, "person_group_code": "A"}
Success Find Document : {"customer_id": 100, "person_group_code": "A"}
Find Thread #3 end
`````

#### Update Test

수정을 위해서 다음과 같이 파일을 수정 합니다.

`````
mongodb.command =U
mongodb.start = 1
mongodb.end = 100
mongodb.endpoint=shardedcluster.5qjlg.mongodb.net/myFirstDatabase?retryWrites=true&w=majority
mongodb.user=user_id
mongodb.pass=password00
mongodb.driver=mongodb+srv://
sleepmi=0
mongodb.print=true
mongodb.collection=shardCustomer
mongodb.database=shardDB

# Find Option
findloop=1

# thread option (find 만 적용)
threadcount=10

# Update Option
mongodb.updatedata=AAAA
`````
Command를 U로 수정 하고 1 ~100 까지의 customer_id를 수정 합니다. 데이터는 도큐먼트내에 updatedata 라는 컬럼의 데이터가 설정한 데이터 AAAA 로 수정 됩니다.
(update 는 single thread 로 동작 합니다.)

#### Update 실행

컴파일 후 jar 파일을 생성 하여 다음과 같이 실행 합니다.
`````
 % java -jar target/MongoDB-samples-1.0.0-jar-with-dependencies.jar command.properties
Update starts *****************
Success Update Document : 1
Success Update Document : 2
Success Update Document : 3
Success Update Document : 4
Success Update Document : 5
...
Success Update Document : 98
Success Update Document : 99
Success Update Document : 100

`````

데이터를 확인 하면 다음과 같이 데이터가 수정된 것을 확인 할 수 있습니다.
<img src="/LoadGenAtlas/images/images02.png" width="70%" height="70%">    

#### Delete Test

삭제을 위해서 다음과 같이 파일을 수정 합니다.

`````
mongodb.command =D
mongodb.start = 1
mongodb.end = 100
mongodb.endpoint=shardedcluster.5qjlg.mongodb.net/myFirstDatabase?retryWrites=true&w=majority
mongodb.user=user_id
mongodb.pass=password00
mongodb.driver=mongodb+srv://
sleepmi=0
mongodb.print=true
mongodb.collection=shardCustomer
mongodb.database=shardDB

# Find Option
findloop=1

# thread option (find 만 적용)
threadcount=10

# Update Option
mongodb.updatedata=AAAA
`````
Command를 D로 수정 하고 삭제는 start, end 로 입력된 1~100 의 customer_id를 삭제 합니다. 
(delete 는 single thread 로 동작 합니다.)


#### Delete 실행

컴파일 후 jar 파일을 생성 하여 다음과 같이 실행 합니다.
`````
 % java -jar target/MongoDB-samples-1.0.0-jar-with-dependencies.jar command.properties
Delete starts *****************
Success Delete Document : 1
Success Delete Document : 2
Success Delete Document : 3
...
Success Delete Document : 98
Success Delete Document : 99
Success Delete Document : 100

`````
데이터를 검색 하면 데이터가 없는 것을 확인 할 수 있습니다.

<img src="/LoadGenAtlas/images/images03.png" width="70%" height="70%">  