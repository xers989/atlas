### Vault - Atlas Login

Atlas 의 Database 접속 계정에 대한 Life cycle을 관리 합니다.
Atlas API 를 이용하여 계정을 생성 하고 TTL을 주어 해당 시간 이후 계정을 disable 하여 줍니다.

#### Vault 구성
Vault 서비스를 준비 합니다. (Docker로 vault를 구동)
Vault를 Dev 모드로 구동하는 경우 접근이 local 만 가능함으로 외부 접근이 가능한 형태 구동이 필요합니다. 서비스 구동을 위해서는 Vault 폴더 및 config 파일이 필요 함으로 다음 파일을 준비 합니다.   

폴더 준비
`````
$ mkdir /vault /vault/file /vault/logs /vault/config

`````

local.json 파일 준비
`````
$ vi /vault/config/local.json
{
  "listener":
  {
    "tcp":
    {
      "address":"0.0.0.0:8200",
      "tls_disable":1
    }
  },
  "backend":
  {
    "file":
    {
      "path":"/vault/file"
    }
  },
  "default_lease_ttl":"168h",
  "max_lease_ttl":"720h",
  "ui":true,
  "log_level":"trace"
}
`````

Docker continer 구동
`````
$ docker run --name atlasvault -d --cap-add=IPC_LOCK -p 8200:8200 --log-opt mode=non-blocking -v /vault/config:/vault/config -v /vault/file:/vault/file -v /vault/logs:/vault/logs vault server

$ docker logs -f atlasvault >>/vault/logs/mystd.log 2>&1&
$ tail -f /vault/logs/mystd.log
`````

Vault UI에 접속 하여 unseal 하여 줍니다.


#### DBA 용 Atlas database 접속 계정 생성

Atlas plugin을 사용하는 vault secret을 구성
(Vault API를 이용한 방법)

Vault Container에 접속하여 Vault API를 호출 합니다.
`````
$ docker exec -it altasvault sh
# export VAULT_ADDR='http://127.0.0.1:8200'
/ # vault login
Token (will be hidden) : <<root token 입력>>
`````

Atlas Database secret 설정
`````
$ vault secrets enable database
$ vault write database/config/atlas-database plugin_name=mongodbatlas-database-plugin allowed_roles="atlas-dba-role" public_key="<PRIKEY>" private_key="<PUBKEY>" project_id="<PROJECT_ID>"

$	vault write database/roles/atlas-dba-role  db_name=atlas-database creation_statements='{ "database_name": "admin", "roles": [{"databaseName":"ecomm","roleName":"read"}]}' 

`````

실행후 vault UI 에 접속 하여 보면 다음과 같이 database가 생성 된 것을 볼 수 있다.   
<img src="/vault/images/images01.png" width="70%" height="70%">    

또한 Role 페이지에서는 생성한 Role 에 대한 정보도 볼 수 있다.    
<img src="/vault/images/images02.png" width="70%" height="70%">    

계정을 생성 하기 위해서 다음을 실행 하여 준다.

`````
$  vault read database/creds/atlas-dba-role
Key                Value
---                -----
lease_id           database/creds/atlas-dba-role/5G2dzp8O68B6aRJ2mWjbpAnm
lease_duration     876000h
lease_renewable    true
password           TWPibLA4Y***********
username           v-atlas-dba-role-9K9
`````

Atlas Console 에 생성 된 계정 정보를 다음과 같이 확인 할 수 있다.    
<img src="/vault/images/images03.png" width="70%" height="70%">  


lease_duration 동안 계정이 유효 하여 이는 조절 될 수 있다.  
TTL (lease_duration)은 다음과 같이 조정 될 수 있다.
<img src="/vault/images/images07.png" width="70%" height="70%">  

계정을 발급 받으면 다음과 같이 시간이 조정 된 것을 볼 수 있다. (lease_duration 이 10분으로 조정 됨)    
`````
/ #  vault read database/creds/atlas-dba-role
Key                Value
---                -----
lease_id           database/creds/atlas-dba-role/ik8xV29ctyC927ncwM9OmsPx
lease_duration     10m
lease_renewable    true
password           9DvSPqUL1F*****
username           v-atlas-dba-role-tzi
`````

Mongosh 을 이용하여 테스트 하면 다음 과 같다.

`````
% mongosh "mongodb+srv://v-atlas-dba-role-9K9:TWPibLA4YC*******@cluster0.5qjlg.mongodb.net/" --apiVersion 1 --authenticationDatabase admin
Current Mongosh Log ID:	628c5d4c807b1d160284d2ef
Connecting to:		mongodb+srv://<credentials>@cluster0.5qjlg.mongodb.net/
Using MongoDB:		5.0.8 (API Version 1)
Using Mongosh:		1.0.5

For mongosh info see: https://docs.mongodb.com/mongodb-shell/

Atlas atlas-t0pzlo-shard-0 [primary] test> 

`````

#### REST API 를 이용한 접속 계정 생성

Rest API를 이용하여 계정을 생성 하기 위해서는 Policy 를 작성 합니다.   

<img src="/vault/images/images04.png" width="70%" height="70%">    

`````
path "database/creds/*" {
  capabilities = ["read","create","update","list"]
}
`````

저장 후 이를 호출 할 수 있는 Credential 을 생성 하여 줍니다. 

`````
$ vault write auth/token/create policies="atlas-dba-policy"
`````

<img src="/vault/images/images05.png" width="70%" height="70%">    

REST API 로 다음과 같이 호출 한다.
`````
% curl --header "X-Vault-Token: hvs.CAESICqsQKrwPtXBuT-m_9f-aXOhiEJsCESXUo3z5NVNYv0XGh4KHGh2cy5****" --request GET  http://129.154.***.***:8200/v1/database/creds/atlas-dba-role
{"request_id":"1be11025-561f-1f70-75c1-7f10384e6637","lease_id":"database/creds/atlas-dba-role/wNJM2iO36Ld5PgTTeyTN76ak","renewable":true,"lease_duration":3153600000,"data":{"password":"QNa-gU85*****","username":"v-atlas-dba-role-xBx"},"wrap_info":null,"warnings":null,"auth":null}
`````

Atlas Console 에 계정이 생성 된 것을 확인 할 수 있다.    
<img src="/vault/images/images06.png" width="70%" height="70%">    

#### Application의 접속 정보 저장 (TTL이 없음)

Application 의 접속 정보 (Connection URL) 저장은 Key-Value를 이용하여 저장 하여야 합니다. (TTL이 없는 형태)
Vault UI 에서 Key-Vaule 를 enable하여 줍니다. (Secret 에서 enable new engine를 선택 후 KV를 선택 합니다.)    
Key-value에 대한 경로를 지정한 후 저장 합니다.   
<img src="/vault/images/images08.png" width="70%" height="70%">    

생성된 KV에 오픈 후 새로운 secret 를 생성 하여 줍니다.
<img src="/vault/images/images09.png" width="70%" height="70%">   
Key-Value 는 경로 정보를 지정하고 해당 경로에 데이터 (key-value)를 생성 하는 것으로 데이터 부분에 ID/Pass 및 connection url 등을 저장 할 수 있습니다. (테스트에서는 ID/Pass 만 저장 합니다.)    
<img src="/vault/images/images10.png" width="70%" height="70%">   

데이터 접근을 위한 Policy를 지정 하여 줍니다.   
<img src="/vault/images/images11.png" width="70%" height="70%">   

`````
vault write auth/token/create policies="mysecret_readonly"

Key             Value                                                                                          
client_token    hvs.CAESIEoSxo-LEP-IGwxD4zTccF1JNontMRfFG-*****
accessor        ixrRE58gpxeDUa110YWd18oC                                                                       
policies        ["default","mysecret_readonly"]                                                                
token_policies  ["default","mysecret_readonly"]                                                                
metadata        null                                                                                           
lease_duration  3153600000                                                                                     
renewable       true                                                                                           
entity_id                                                                                                      
token_type      service                                                                                        
orphan          false                                                                                          
mfa_requirement null                                                                                           
num_uses        0                                                                                              
`````

데이터의 추출은 다음과 같이 진행 합니다.      
`````
/ # vault login
Token (will be hidden):  <<client_token>>
Success! You are now authenticated. The token information displayed below
is already stored in the token helper. You do NOT need to run "vault login"
again. Future Vault requests will automatically use this token.
/ # vault kv get kv/mysecret
== Secret Path ==
kv/data/mysecret

======= Metadata =======
Key                Value
---                -----
created_time       2022-05-25T00:26:01.453469743Z
custom_metadata    <nil>
deletion_time      n/a
destroyed          false
version            1

==== Data ====
Key     Value
---     -----
id      admin
pass    *******
`````

API 를 이용한 추출은 다음과 같다.
`````
 % curl --header "X-Vault-Token: hvs.CAESIIZzIMmORgs4Cy1RirlbcOOCPC21PpSYR3T1q1R97DYAGh4KHGh2cy4yQ***" --request GET  http://129.154.***.***:8200/v1/kv/data/mysecret      
{"request_id":"d4bb02cf-6bd8-93a4-97fb-d2db5ec66df1","lease_id":"","renewable":false,"lease_duration":0,"data":{"data":{"id":"admin","pass":"****"},"metadata":{"created_time":"2022-05-25T00:26:01.453469743Z","custom_metadata":null,"deletion_time":"","destroyed":false,"version":1}},"wrap_info":null,"warnings":null,"auth":null}
`````

#### Nodejs 에서 사용하기

Nodejs application을 생성 후 다음을 설치 하여 준다.
`````
$ npm install node-vault
`````

Vault 와 연결 및 키 데이터를 가져오도록 준비 한다.
`````
const readkey= <<policy_token>>;

const vault_options = {
    apiVersion: 'v1', 
    endpoint: 'http://129.154.***.***:8200',
    token: readkey
  };

const vault = require("node-vault")( vault_options);

vault.read('kv/data/mysecret').then(v => {
    connectionString = "mongodb+srv://"+v.data.data.id+":"+v.data.data.pass+"@"+mongodb;
    client = new MongoClient(connectionString);
    
}).catch(e => console.error(e));
...

router.route('/').get( async(req, res, next) => {
    try{
        await client.connect();
        const database = client.db(databasename);
        const handson = database.collection("mycollection");
        const query = {};
        const cursor = await handson.find(query);
        
        const results = await cursor.toArray();
        let outcomes = '';
        if (results.length > 0) {
            results.forEach((result, i) => {
                outcomes += JSON.stringify(result);
                console.log(result);
            });
        } else {
            console.log('No Data');
        }

        console.log("Outcomes : "+outcomes);
        res.status(200).json(results);

    } catch(e)
    {
        console.log("Error");
        console.error(e);
        res.status(404).json({});

    }
    finally{
        await client.close();
    }    
})

`````

REST API 를 호출은 다음과 같이 잘 되는 것을 볼 수 있다.   

<img src="/vault/images/images12.png" width="70%" height="70%">   