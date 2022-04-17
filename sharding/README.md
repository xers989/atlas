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
