### Atlas Data Locality

애플리케이션이 읽기 쓰기 진행시 low latency 및 데이터 주권을 고려한 장소로 연결 합니다.
Multi region 으로 클러스터를 구성하고 레이턴시를 고려한 읽기 쓰기를 위해 Shard 를 구성 합니다.

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
