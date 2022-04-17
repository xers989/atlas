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

#### Database & Collection 생성

클러스터가 생성이 완료된 이후 데이터 베이스를 생성 합니다. 
다음과 같이 데이터베이스(ShardDatabase)를 생성하고 컬렉션(LocalityCollection)을 생성 합니다.


<img src="/data-locality/images/images06.png" width="70%" height="70%">  

Shard Key 로 customer_id를 입력하여 줍니다.
Shard Key 는 반드시 지역 정보를 포함하고 있어야 합니다. (location) 첫번 째 shard key 는 데이터 분산을 위한 조건으로 location으로 설정 하거나 customer id 를 기준으로 지정 할 수 있습니다. ()

#### Data Import & Location 확인

Compass 를 실행 하여 데이터 (demo-data.json)를 import 하여 줍니다.   

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

내용 처럼 미주에 있는 데이터만 존재 하는 것을 확인 할 수 있습니다. (Primary 노드가 아닌 Secondary 노드를 선택 하면 연결은 가능하나 조회 할 때 MongoServerError: not primary and secondaryOk=false가 나오게 됩니다. Secondary에 읽기 설정이 않되어 있음으로 Primary에 연결하여 데이터를 확인합니다.)
데이터는 location 을 기준으로 각 Shard 에 저장이 됩니다.   
동일한 방법으로 EMEA, APAC의 Primary에 로그인 하여 데이터를 확인 합니다.   

EMEA 로 연결하여 확인 한 데이터   
`````
Atlas atlas-10lm9v-shard-1 [direct: primary] test> use ShardDatabase
switched to db ShardDatabase
Atlas atlas-10lm9v-shard-1 [direct: primary] ShardDatabase> db.LocalityCollection.find()
Browserslist: caniuse-lite is outdated. Please run:
  npx browserslist@latest --update-db
  Why you should do it regularly: https://github.com/browserslist/browserslist#browsers-data-updating
[
  {
    _id: ObjectId("625af6b55a86fbda386d5ed8"),
    customer_id: '887867153',
    firstName: 'Patrick',
    lastName: 'Dainelli',
    address: '1084 Jihon Way',
    city: 'Ojzegoj',
    state: 'UT',
    zip: '48609',
    location: 'IT',
    age: 34
  },
  {
    _id: ObjectId("625af6b55a86fbda386d5ed7"),
    customer_id: '912563881',
    firstName: 'Johanna',
    lastName: 'Lepri',
    address: '1521 Binuc Highway',
    city: 'Viatop',
    state: 'IL',
    zip: '82708',
    location: 'IT',
    age: 50
  },
  {
    _id: ObjectId("625af6b55a86fbda386d5edd"),
    customer_id: '142964413',
    firstName: 'Max',
    lastName: 'Hodgson',
    address: '514 Pitac Lane',
    city: 'Gidgofcu',
    state: 'MI',
    zip: '31254',
    location: 'IT',
    age: 28
  },
`````


#### Shard 설정 확인

Mongosh 로 접속하여 shard 의 정보를 확인 합니다.
Shards 에 3개의 Shard 가 구성 되어 있는 것을 확인 할 수 있으며 각 Shard별로 Tag 가 생성 되어 있습니다.
atlas-10lm9v-shard-0 는 Americas Shard 로 tag 값이 625aefcb7d7092637401050b 으로 되어 있으며 Collectios 에서 CA 의 경우 Tag 값이 625aefcb7d7092637401050b (Americas)로 설정 되어 있는 것을 확인 할 수 있습니다.   

`````
% mongosh mongodb+srv://cluster1.5qjlg.mongodb.net/myFirstDatabase --username ****
Enter password: **********

Atlas [mongos] myFirstDatabase> sh.status()
shardingVersion
{
  _id: 1,
  minCompatibleVersion: 5,
  currentVersion: 6,
  clusterId: ObjectId("625af3328fe252c8985fbc24")
}
shards
[
  {
    _id: 'atlas-10lm9v-shard-0',
    host: 'atlas-10lm9v-shard-0/atlas-10lm9v-shard-00-00.5qjlg.mongodb.net:27017,atlas-10lm9v-shard-00-01.5qjlg.mongodb.net:27017,atlas-10lm9v-shard-00-02.5qjlg.mongodb.net:27017',
    state: 1,
    topologyTime: Timestamp({ t: 1650127701, i: 2 }),
    tags: [ '625aefcb7d7092637401050b' ]
  },
  {
    _id: 'atlas-10lm9v-shard-1',
    host: 'atlas-10lm9v-shard-1/atlas-10lm9v-shard-01-00.5qjlg.mongodb.net:27017,atlas-10lm9v-shard-01-01.5qjlg.mongodb.net:27017,atlas-10lm9v-shard-01-02.5qjlg.mongodb.net:27017',
    state: 1,
    topologyTime: Timestamp({ t: 1650127705, i: 2 }),
    tags: [ '625aefcb7d7092637401050c' ]
  },
  {
    _id: 'atlas-10lm9v-shard-2',
    host: 'atlas-10lm9v-shard-2/atlas-10lm9v-shard-02-00.5qjlg.mongodb.net:27017,atlas-10lm9v-shard-02-01.5qjlg.mongodb.net:27017,atlas-10lm9v-shard-02-02.5qjlg.mongodb.net:27017',
    state: 1,
    topologyTime: Timestamp({ t: 1650127712, i: 1 }),
    tags: [ '625aefcb7d7092637401050d' ]
  }
]
...
  {
    database: {
      _id: 'ShardDatabase',
      primary: 'atlas-10lm9v-shard-2',
      partitioned: true,
      version: {
        uuid: UUID("f8f940fc-e8d5-422b-a747-38a2cb60761c"),
        timestamp: Timestamp({ t: 1650127945, i: 15 }),
        lastMod: 1
      }
    },
    collections: {
      'ShardDatabase.LocalityCollection': {
        shardKey: { location: 1, customer_id: 1 },
        unique: false,
        balancing: true,
        chunkMetadata: [
          { shard: 'atlas-10lm9v-shard-0', nChunks: 166 },
          { shard: 'atlas-10lm9v-shard-1', nChunks: 315 },
          { shard: 'atlas-10lm9v-shard-2', nChunks: 269 }
        ],
        chunks: [
          'too many chunks to print, use verbose if you want to force print'
        ],
        tags: [
          {
            tag: '625aefcb7d7092637401050c',
            min: { location: 'AD', customer_id: MinKey() },
            max: { location: 'AE', customer_id: MinKey() }
          },
          {
            tag: '625aefcb7d7092637401050c',
            min: { location: 'AE', customer_id: MinKey() },
            max: { location: 'AE-AJ', customer_id: MinKey() }
          },
          ..
          {
            tag: '625aefcb7d7092637401050b',
            min: { location: 'BZ', customer_id: MinKey() },
            max: { location: 'CA', customer_id: MinKey() }
          },

`````

#### Zone 설정 변경

3개의 Zone 을 4개로 수정 합니다. (인도 추가)
Cluster 정보에서 Edit Configuration 을 선택 합니다.  

<img src="/data-locality/images/images12.png" width="70%" height="70%">  

수정 화면에서 Add a Zone으로 Zone을 추가하고 Recommended region 에서 Mumbai를 선택 합니다.
<img src="/data-locality/images/images13.png" width="70%" height="70%">  

추가된 Zone 이 지도에 표기 되며 자동으로 Zone 영역이 생성 됩니다.
<img src="/data-locality/images/images14.png" width="70%" height="70%">  

설정을 완료 하고 적용 하여 줍니다.

클러스터 정보가 변경 되고 적용이 됩니다.

설정이 완료 되고 Cluster 정보를 보면 다음과 같이 Zone1이 추가 된 것을 확인 할 수 있습니다.
<img src="/data-locality/images/images15.png" width="70%" height="70%">  

Zone1 의 Primary 노드에 연결하여 데이터를 확인 하여 봅니다. (India 지역 데이터)


`````
% mongosh mongodb://cluster1-shard-03-02.5qjlg.mongodb.net:27017 --tls --authenticationDatabase admin --username ****
Enter password: **********

Atlas atlas-10lm9v-shard-3 [direct: primary] test> use ShardDatabase
switched to db ShardDatabase
Atlas atlas-10lm9v-shard-3 [direct: primary] ShardDatabase> db.LocalityCollection.find()
[
  {
    _id: ObjectId("625af6b55a86fbda386d5eef"),
    customer_id: '475131731',
    firstName: 'Jeremy',
    lastName: 'van den Ploeg',
    address: '1082 Lobit Pike',
    city: 'Tandaub',
    state: 'MO',
    zip: '31095',
    location: 'IN',
    age: 61
  },
  {
    _id: ObjectId("625af6b55a86fbda386d5ee3"),
    customer_id: '691033014',
    firstName: 'Joe',
    lastName: 'Donati',
    address: '873 Pimav River',
    city: 'Buidis',
    state: 'HI',
    zip: '34869',
    location: 'IN',
    age: 29
  },
  {
    _id: ObjectId("625af6b55a86fbda386d5ee8"),
    customer_id: '455842639',
    firstName: 'Mathilda',
    lastName: 'Giorgi',
    address: '392 Kana Center',
    city: 'Fenaruhik',
    state: 'CT',
    zip: '78346',
    location: 'IN',
    age: 18
  },
  {
    _id: ObjectId("625af6b55a86fbda386d5f31"),
    customer_id: '981513122',
    firstName: 'Alan',
    lastName: 'Pasquini',
    address: '848 Egzic Grove',
    city: 'Jaeniil',
    state: 'KY',
    zip: '53406',
    location: 'IN',
    age: 58
  },
`````

정상적으로 인도 지역의 데이터가 기존 다른 Zone 에서 이동 된 것을 확인 할 수 있습니다.

#### Zone 내부에 읽기 지역 설정
Americas 로 설정 된 Replica Set 에 대해 읽기 부분을 추가 하는 것으로 타지역에 노드를 추가 하여 줍니다.
Virginia 에 생성 된 Zone 에 대해사 설정을 변경을 위해 Zone Configuration 에서 multi-region, multi-cloud 를 On 하여 줍니다.

<img src="/data-locality/images/images15.png" width="70%" height="70%"> 

읽기 전용 노드로 Seoul 에 노드를 추가 하여 줍니다.    
<img src="/data-locality/images/images15.png" width="70%" height="70%"> 

설정을 완료 하면 Seoul 에 Americas 의 읽기 전용 노드가 생성 됩니다.

#### Zone 의 Shard 추가
현재는 각 지역(Zone)별로 한개의 Shard가 구성 되어 있습니다. 해당 지역에 대해 Shard 를 추가 할 수 있습니다. 클러스터의 설정 정보 변경에서 Americas 를 선택 합니다.
Additional Options 에 현재 1로 설정 된 Shard 를 2로 추가 하여 주면 Shard가 2개로 구성 됩니다.     


<img src="/data-locality/images/images15.png" width="70%" height="70%"> 