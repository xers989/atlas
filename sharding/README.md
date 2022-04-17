### Sharding

Shard를 구성하고 Shard Key를 지정 합니다.
문서의 매뉴얼은 다음을 참고 합니다.   
https://www.mongodb.com/docs/atlas/atlas-ui/collections/

#### Shard 구성
Atlas Console에 로그인 한 후 샤드 클러스터를 구성 합니다.   
구성은 최소 M30이며 AWS의 서울 지역에 생성 합니다.   
Additional Settings에 Shard Option 을 On으로 변경 하여 줍니다.
<img src="/sharding/images/images01.png" width="70%" height="70%"> 

Shard 구성은 2개로 하여 줍니다.   
<img src="/sharding/images/images02.png" width="70%" height="70%"> 

클러스터를 생성 하여 줍니다.

생성된 클러스터 정보를 확인 하면 2개의 Shard 가 구성된 것을 확인 할 수 있습니다.
<img src="/sharding/images/images03.png" width="70%" height="70%"> 

#### Database & Collection 구성
mongosh 로 접속 한 후에 데이터 베이스를 생성 하고 컬렉션을 생성 하여 줍니다. 연결하기 위한 정보는 Database cluster 에 Connect에서 확인 할 수 있습니다.
<img src="/sharding/images/images04.png" width="70%" height="70%"> 

접속 후 Database 와 Collection 을 생성 한 후 Shard 를 enable 하여 줍니다.
`````
% mongosh "mongodb+srv://shardedcluster.5qjlg.mongodb.net/myFirstDatabase" --apiVersion 1 --username ****
Enter password: **********

Atlas [mongos] myFirstDatabase> use shardDB
switched to db shardDB
Atlas [mongos] shardDB> db.createCollection("shardCustomer")

Atlas [mongos] shardDB> sh.enableSharding("shardDB")
{
  ok: 1,
  '$clusterTime': {
    clusterTime: Timestamp({ t: 1650196244, i: 1 }),
    signature: {
      hash: Binary(Buffer.from("72e70d3f088153d7f38402bc5c68e233c0d268c2", "hex"), 0),
      keyId: Long("7087535176225390615")
    }
  },
  operationTime: Timestamp({ t: 1650196244, i: 1 })
}

`````
Hashed 를 이용한 샤드 구성을 위해 인덱스를 사전에 선언 하고 컬렉션에 Shard 설정을 하여 줍니다.
입력할 데이터는 다음과 같은 형태로 customer_id 를 기준으로 하여 hash key를 구성합니다.
`````
{"customer_id":"114380130","firstName":"Jorge","lastName":"Maruyama","address":"243 Awadiz Circle","city":"Gobema","state":"ND","zip":"04692","location":"JP","age":31}
`````
컬렉션을 설정 합니다.
`````
Atlas [mongos] shardDB> db.shardCustomer.createIndex({customer_id:"hashed"})
customer_id_hashed

Atlas [mongos] shardDB> sh.shardCollection("shardDB.shardCustomer",{customer_id:"hashed"})
{
  collectionsharded: 'shardDB.shardCustomer',
  ok: 1,
  '$clusterTime': {
    clusterTime: Timestamp({ t: 1650196249, i: 16 }),
    signature: {
      hash: Binary(Buffer.from("e8c3c8dd65231b18cb9bd78ba9562cebfad4110f", "hex"), 0),
      keyId: Long("7087535176225390615")
    }
  },
  operationTime: Timestamp({ t: 1650196249, i: 9 })
}

`````

#### Data import 와 분산 확인

Compass 를 이용하여 연결 후 데이터(demo-data.json)를 import 하여 줍니다.   

<img src="/sharding/images/images05.png" width="70%" height="70%"> 

Json 형태로 하여 import를 진행 합니다.

<img src="/sharding/images/images06.png" width="70%" height="70%"> 

데이터 import가 완료 된 화면


<img src="/sharding/images/images07.png" width="70%" height="70%"> 

Mongosh 에 접속하여 Data 분산 내용을 확인 합니다.

`````
% mongosh "mongodb+srv://shardedcluster.5qjlg.mongodb.net/myFirstDatabase" --apiVersion 1 --username ***
Enter password: **********

Atlas [mongos] myFirstDatabase> 

Atlas [mongos] myFirstDatabase> use shardDB
switched to db shardDB

Atlas [mongos] shardDB> db.shardCustomer.getShardDistribution()
Shard atlas-27u9bh-shard-0 at atlas-27u9bh-shard-0/atlas-27u9bh-shard-00-00.5qjlg.mongodb.net:27017,atlas-27u9bh-shard-00-01.5qjlg.mongodb.net:27017,atlas-27u9bh-shard-00-02.5qjlg.mongodb.net:27017
{
  data: '51KiB',
  docs: 270,
  chunks: 2,
  'estimated data per chunk': '25KiB',
  'estimated docs per chunk': 135
}
---
Shard atlas-27u9bh-shard-1 at atlas-27u9bh-shard-1/atlas-27u9bh-shard-01-00.5qjlg.mongodb.net:27017,atlas-27u9bh-shard-01-01.5qjlg.mongodb.net:27017,atlas-27u9bh-shard-01-02.5qjlg.mongodb.net:27017
{
  data: '43KiB',
  docs: 230,
  chunks: 2,
  'estimated data per chunk': '21KiB',
  'estimated docs per chunk': 115
}
---
Totals
{
  data: '95KiB',
  docs: 500,
  chunks: 4,
  'Shard atlas-27u9bh-shard-0': [
    '54.05 % data',
    '54 % docs in cluster',
    '195B avg obj size on shard'
  ],
  'Shard atlas-27u9bh-shard-1': [
    '45.94 % data',
    '46 % docs in cluster',
    '195B avg obj size on shard'
  ]
}
`````
총 500 개의 문서가 있으며 Shard1 과 Shard2에 각각 270 개, 230개 가 있는 것을 확인 할 수 있습니다.