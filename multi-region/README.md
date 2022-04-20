### Atlas Multi Region

클라우드간 혹은 데이터 센터간에 클로한 형태로 클러스터를 구성합니다.   
Auto-failover 로 구성하여 maximum high availability를 구현    



클러스터의 Replica set 구성시 다른 member 를 다른 region 에 구성    
다음과 같이 3개의 다른 지역을 사용하여 Replica Set 을 구성합니다. (서울, 도쿄, 홍콩)   
<img src="/multi-region/images/images06.png" width="70%" height="70%"> 


자동으로 클라이언트 애플리케이션의 mongodb driver 는 primary host 를 찾아서 접근 하며 fail over 시 자동으로 변경 합니다.   
서울,홍콩,도쿄 간의 High Availability 를 구성 합니다.   
#### Replica 구성

MongoDB는 3.6 이상으로 구성 합니다.   
Atlas Console 로그인   

New Cluster 를 클릭 하고 배포를 할 지역을 선택 합니다. (서울 선택)    
<img src="/multi-region/images/images02.png" width="70%" height="70%"> 

Cloud Provider & Region 을 선택 후 Multi-Cloud, Multi-Region & Workload Isolation 을 On 하여 줍니다.    
<img src="/multi-region/images/images01.png" width="70%" height="70%">  

배포할 노드의 개수를 조정 합니다. (1개로 수정)    
<img src="/multi-region/images/images03.png" width="70%" height="70%">  

Add a provider/region 을 클릭하여 노드를 추가 하여 줍니다.
<img src="/multi-region/images/images04.png" width="70%" height="70%">  

리스트의 순서에 따라 Primary 에 대한 우선순위가 지정 됩니다. 리스트 맨앞 아이콘을 드래그 하여 순서를 조정 할 수 있습니다.    

추가로 노드를 구성 할 수 있으며 Read-only, Analytics nodes 로 추가 구성 할 수 있습니다.    
<img src="/multi-region/images/images05.png" width="70%" height="70%">  


#### Insert 테스트

생성된 Replica Set 에 데이터를 생성 하여 Replication 에 대한 테스트를 진행 합니다.  지역간 네트워크를 보기 위해 다음과 같이 지역을 조정하여 진행합니다.   
<img src="/multi-region/images/images07.png" width="70%" height="70%">  

서울 지역에서 데이터를 읽기 쓰기를 진행 하며 전체 노드에 데이터를 바로 입력 하는 형태로 진행 합니다. (MongoDB의 쓰기 옵션으로 조정 가능한 항목, writeConcern을 3으로 하여 3개 노드 모두에 데이터가 생성 되어야 하는 조건으로 합니다)

다음은 Atlas 와 연결 하기 위한 connection String 입니다.
`````
mongodb+srv://multicluster.5qjlg.mongodb.net/myFirstDatabase?retryWrites=true&w=3

`````

w=3 은 3개 노드 (서울, 일본, 샌프란 시스코)에 모두에 데이터가 작성 되어야 하는 것을 의미 합니다.

100개의 데이터 생성을 진행 합니다.
`````
$ java -jar MongoDB-samples-1.0.0-jar-with-dependencies.jar command.properties
Insertion starts *****************
Success Insert Document : 1,1205
Success Insert Document : 2,148
Success Insert Document : 3,272
Success Insert Document : 4,143
Success Insert Document : 5,272
Success Insert Document : 6,143
Success Insert Document : 7,272
Success Insert Document : 8,144
Success Insert Document : 9,272
Success Insert Document : 10,143
...
Success Insert Document : 94,143
Success Insert Document : 95,272
Success Insert Document : 96,142
Success Insert Document : 97,272
Success Insert Document : 98,143
Success Insert Document : 99,155
Success Insert Document : 100,253
Insertion Ends *****************21575
`````
데이터는 입력된 결과와 번호 그리고 응답 시간(ms) 입니디. 첫번째 데이터의 경우 1205로 연결을 MongoDB와 Connection 이 되는 것을 포함하여 시간이 길게 나옴니다. 네트워크 상태에 따라 결과가 다를 수 있으나 서울을 primary 로 하고 2개 secondary에 바로 저장 하는 구조로 140 ~ 270의 응답 시간이 보여 집니다. (평균 약 200ms)

미국 산호세의 서버에서 동일한 테스트를 진행 하는 경우 그 결과는 다음과 같습니다.
`````
$ java -jar MongoDB-samples-1.0.0-jar-with-dependencies.jar command.properties 
Insertion starts *****************
Success Insert Document : 1001,2013
Success Insert Document : 1002,279
Success Insert Document : 1003,276
Success Insert Document : 1004,275
Success Insert Document : 1005,276
Success Insert Document : 1006,275
Success Insert Document : 1007,275
Success Insert Document : 1008,275
Success Insert Document : 1009,275
Success Insert Document : 1010,276
...
Success Insert Document : 1098,274
Success Insert Document : 1099,274
Success Insert Document : 1100,275
Insertion Ends *****************29219
`````

데이터 중복을 방지 하기 위해 1001 ~ 1100 번까지 100개의 데이터를 생성 하였으며 서울이 Primary 이기 때문에 응답시간이 조금 더 길어지는 형태로 275ms가 나오는 것을 볼 수 있습니다.

Writeconcern을 2로 조정 하는 경우 한개 secondary 노드는 지연된 쓰기 작업으로 데이터가 동기화 되는데 지연이 발생 할수 있습니다. 그러나 쓰기 속도는 평균 60, 산호세는 평균 173 수준으로 개선 됩니다.

#### Read 테스트
생성된 Replica Set 의 데이터를 읽기 작업을 진행 합니다. 서울의 경우 Primary 가 위치한 곳임으로 빠르게 읽기가 진행 됩니다.
다음은 MongoDB 와 연결을 위한 Connection String 정보 입니다.
`````
mongodb+srv://multicluster.5qjlg.mongodb.net/myFirstDatabase?retryWrites=true&w=3
`````
한국에서 100 개의 데이터를 읽기 작업 한 경우 결과는 다음과 같습니다.

`````
[opc@docker-server ~]$ java -jar MongoDB-samples-1.0.0-jar-with-dependencies.jar command.properties
Find Thread #0 start
Success Find Document : {"customer_id": 1, "person_group_code": "A"},922
Success Find Document : {"customer_id": 2, "person_group_code": "A"},6
Success Find Document : {"customer_id": 3, "person_group_code": "A"},7
Success Find Document : {"customer_id": 4, "person_group_code": "A"},6
Success Find Document : {"customer_id": 5, "person_group_code": "A"},5
Success Find Document : {"customer_id": 6, "person_group_code": "A"},5
Success Find Document : {"customer_id": 7, "person_group_code": "A"},6
Success Find Document : {"customer_id": 8, "person_group_code": "A"},5
Success Find Document : {"customer_id": 9, "person_group_code": "A"},5
Success Find Document : {"customer_id": 10, "person_group_code": "A"},5
...
Success Find Document : {"customer_id": 96, "person_group_code": "A"},5
Success Find Document : {"customer_id": 97, "person_group_code": "A"},4
Success Find Document : {"customer_id": 98, "person_group_code": "A"},4
Success Find Document : {"customer_id": 99, "person_group_code": "A"},5
Success Find Document : {"customer_id": 100, "person_group_code": "A"},4
Find Thread #0 end
`````
읽기의 결과가 5ms 정도로 빠르게 읽기가 진행 되는 것을 볼 수 있습니다. (처음 읽기 작업은 MongoDB와 연결을 하는 부분이 포함되어 922ms 가 나옵니다)

미국에서 동일한 테스트를 진행 한 결과는 다음과 같습니다.

`````
$ java -jar MongoDB-samples-1.0.0-jar-with-dependencies.jar command.properties 
Find Thread #0 start
Success Find Document : {"customer_id": 1, "person_group_code": "A"},1792
Success Find Document : {"customer_id": 2, "person_group_code": "A"},138
Success Find Document : {"customer_id": 3, "person_group_code": "A"},135
Success Find Document : {"customer_id": 4, "person_group_code": "A"},136
Success Find Document : {"customer_id": 5, "person_group_code": "A"},135
Success Find Document : {"customer_id": 6, "person_group_code": "A"},135
Success Find Document : {"customer_id": 7, "person_group_code": "A"},137
Success Find Document : {"customer_id": 8, "person_group_code": "A"},139
Success Find Document : {"customer_id": 9, "person_group_code": "A"},135
Success Find Document : {"customer_id": 10, "person_group_code": "A"},135
...
Success Find Document : {"customer_id": 97, "person_group_code": "A"},133
Success Find Document : {"customer_id": 98, "person_group_code": "A"},134
Success Find Document : {"customer_id": 99, "person_group_code": "A"},134
Success Find Document : {"customer_id": 100, "person_group_code": "A"},133
Find Thread #0 end

`````
서울에서 읽기 작업이 진행 되기 때문에 읽기 시간이 평균 약 135ms 가 소요 됩니다.

다음은 읽기를 조정하여 가장 가까운 곳에서 읽기 (nearest)로 조정 합니다. Primary, Secondary 를 포함하여 가장 가까운 (응답시간이 빠른)곳으로 읽기 연결을 하는 것임니다. 3개 지역에 모두 동일한 데이터가 존재 함으로 데이터 읽기에 대한 불일치는 없습니다.

연결을 위한 String은 다음과 같습니다.
`````
mongodb+srv://multicluster.5qjlg.mongodb.net/myFirstDatabase?retryWrites=true&w=3&readpreference=nearest

`````

서울의 경우 성능에 차이가 없음으로 산호세에서 테스트를 진행 합니다.

`````
$ java -jar MongoDB-samples-1.0.0-jar-with-dependencies.jar command.properties 
Find Thread #0 start
Success Find Document : {"customer_id": 1, "person_group_code": "A"},869
Success Find Document : {"customer_id": 2, "person_group_code": "A"},14
Success Find Document : {"customer_id": 3, "person_group_code": "A"},5
Success Find Document : {"customer_id": 4, "person_group_code": "A"},6
Success Find Document : {"customer_id": 5, "person_group_code": "A"},5
Success Find Document : {"customer_id": 6, "person_group_code": "A"},13
Success Find Document : {"customer_id": 7, "person_group_code": "A"},5
Success Find Document : {"customer_id": 8, "person_group_code": "A"},6
Success Find Document : {"customer_id": 9, "person_group_code": "A"},5
Success Find Document : {"customer_id": 10, "person_group_code": "A"},5
...
Success Find Document : {"customer_id": 97, "person_group_code": "A"},3
Success Find Document : {"customer_id": 98, "person_group_code": "A"},4
Success Find Document : {"customer_id": 99, "person_group_code": "A"},6
Success Find Document : {"customer_id": 100, "person_group_code": "A"},3
Find Thread #0 end

`````
결과와 같이 약 6 ms 정도로 빠른 읽기 성능이 보여 집니다.
