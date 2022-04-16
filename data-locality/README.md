### Atlas Data Locality

애플리케이션이 읽기 쓰기 진행시 low latency 및 데이터 주권을 고려한 장소로 연결 합니다.
Multi region 으로 클러스터를 구성하고 레이턴시를 고려한 읽기 쓰기를 위해 Shard 를 구성 합니다.


구분된 존은 샤드로 구분된 Replica Set 으로 데이터가 저장 됩니다.    

<img src="/data-locality/images/images05.png" width="70%" height="70%">  

존 구성은 3개로 구성 (Americas, EMEA, APAC)합니다.

#### Shard 구성

MongoDB는 3.6 이상으로 구성 합니다.   
Atlas Console 로그인   

New Cluster 를 클릭 하고 Global Cluster Configuration를 클릭하고 Enable Global Writes 를 on 하여 줍니다.

<img src="/data-locality/images/images01.png" width="70%" height="70%">  

Start Template 으로 Global Performance 혹은 Excellent Global Performance 를 선택 할 수 있습니다.  

<img src="/data-locality/images/images02.png" width="70%" height="70%">  

Global Performance는 3개 지역으로 구분 되며 Configure Zones Myself로 Zone 정보를 수정 구성 할 수 있습니다.

구성 된 Zone 의 정보는 다음과 같습니다.

<img src="/data-locality/images/images03.png" width="70%" height="70%">  


각 Zone 은 하나씩 선택하여 편집 할 수 있수 있습니다.

<img src="/data-locality/images/images04.png" width="70%" height="70%">  

클러스터가 생성이 완료된 이후 데이터 베이스를 생성 합니다. 
다음과 같이 데이터베이스(ShardDatabase)를 생성하고 컬렉션(LocalityCollection)을 생성 합니다.


<img src="/data-locality/images/images06.png" width="70%" height="70%">  

Shard Key 로 customer_id를 입력하여 줍니다.

Compass 를 실행 하여 데이터를 import 하여 줍니다.   

<img src="/data-locality/images/images07.png" width="70%" height="70%">  

임포트가 완료 되면 다음과 같은 형태의 데이터가 생성 됩니다.    

<img src="/data-locality/images/images08.png" width="70%" height="70%">  

데이터를 확인 하기 위해 Cloud Console에 로그인 한 후 Cluster database를 클릭 합니다.    

<img src="/data-locality/images/images10.png" width="70%" height="70%">  

생성된 3개의 Shard 정보를 볼 수 있으며 Americas의 정보를 보기 위해 다음을 클릭 합니다.    

<img src="/data-locality/images/images11.png" width="70%" height="70%">  

주소 줄을 복사하여 줍니다.    
<img src="/data-locality/images/images09.png" width="70%" height="70%">  

직접 해당 데이터 베이스로 접속하여 데이터를 확인 합니다.

`````
% mongosh mongodb://cluster1-shard-00-01.5qjlg.mongodb.net:27017 --tls --authenticationDatabase admin --username admin 
Enter password: **********
Current Mongosh Log ID:	625af7b0a0dcb005c2a331ea
Connecting to:		mongodb://cluster1-shard-00-01.5qjlg.mongodb.net:27017/?directConnection=true
Using MongoDB:		5.0.7
Using Mongosh:		1.0.5
Atlas atlas-10lm9v-shard-0 [direct: primary] test> use ShardDatabase
switched to db ShardDatabase
Atlas atlas-10lm9v-shard-0 [direct: primary] ShardDatabase> db.LocalityCollection.find()
Browserslist: caniuse-lite is outdated. Please run:
  npx browserslist@latest --update-db
  Why you should do it regularly: https://github.com/browserslist/browserslist#browsers-data-updating
[
  {
    _id: ObjectId("625af6b55a86fbda386d5ed9"),
    customer_id: '359376351',
    firstName: 'Elsie',
    lastName: 'Geri',
    address: '955 Aviket Grove',
    city: 'Wofcofeb',
    state: 'CT',
    zip: '12157',
    location: 'US',
    age: 60
  },
  {
    _id: ObjectId("625af6b55a86fbda386d5ef8"),
    customer_id: '737930174',
    firstName: 'Alvin',
    lastName: 'van der Meer',
    address: '544 Leju Junction',
    city: 'Vivifhu',
    state: 'TX',
    zip: '89932',
    location: 'CA',
    age: 65
  },
  {
    _id: ObjectId("625af6b55a86fbda386d5ef9"),
    customer_id: '574312353',
    firstName: 'Kate',
    lastName: 'Sanesi',
    address: '1910 Vagjir Parkway',
    city: 'Hencecu',
    state: 'MD',
    zip: '22678',
    location: 'US',
    age: 30
  },
  {
    _id: ObjectId("625af6b55a86fbda386d5f00"),
    customer_id: '701046179',
    firstName: 'Kathryn',
    lastName: 'Wagenaar',
    address: '131 Furupa Grove',
    city: 'Zirujip',
    state: 'HI',
    zip: '01611',
    location: 'CA',
    age: 59
  },
`````

내용 처럼 미주에 있는 데이터만 존재 하는 것을 확인 할 수 있습니다.
데이터는 location 을 기준으로 각 Shard 에 저장이 됩니다.