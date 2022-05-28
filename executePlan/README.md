### MongoDB Atlas ExecutePlan & Optimization

쿼리에 대한 플랜을 이해 하고 최적화를 진행 합니다. 테스트 진행은 샘플 데이터의 mflix.movies를 사용 합니다.

#### Query 의 실행 (CollScan)

Movies는 기본적으로 Index 설정이 없습니다. 다음 Query를 실행 하면 Full scan 이 진행 됩니다.

`````
db.movies.find({rated:"UNRATED"}).explain()
{
  explainVersion: '1',
  queryPlanner: {
    namespace: 'sample_mflix.movies',
    indexFilterSet: false,
    parsedQuery: { rated: { '$eq': 'UNRATED' } },
    maxIndexedOrSolutionsReached: false,
    maxIndexedAndSolutionsReached: false,
    maxScansToExplodeReached: false,
    winningPlan: {
      stage: 'COLLSCAN',
      filter: { rated: { '$eq': 'UNRATED' } },
      direction: 'forward'
    },
    rejectedPlans: []
  },
  command: {
    find: 'movies',
    filter: { rated: 'UNRATED' },
    '$db': 'sample_mflix'
  },
  serverInfo: {
    host: 'atlas-a1hth3-shard-00-02.5qjlg.mongodb.net',
    port: 27017,
    version: '5.0.7',
    gitVersion: 'b977129dc70eed766cbee7e412d901ee213acbda'
  },
  serverParameters: {
    internalQueryFacetBufferSizeBytes: 104857600,
    internalQueryFacetMaxOutputDocSizeBytes: 104857600,
    internalLookupStageIntermediateDocumentMaxSizeBytes: 104857600,
    internalDocumentSourceGroupMaxMemoryBytes: 104857600,
    internalQueryMaxBlockingSortMemoryUsageBytes: 104857600,
    internalQueryProhibitBlockingMergeOnMongoS: 0,
    internalQueryMaxAddToSetBytes: 104857600,
    internalDocumentSourceSetWindowFieldsMaxMemoryBytes: 104857600
  },
  ok: 1,
  '$clusterTime': {
    clusterTime: Timestamp({ t: 1650898981, i: 1 }),
    signature: {
      hash: Binary(Buffer.from("200999cf4ddf6a90d3f072059f80e215cf8a67f8", "hex"), 0),
      keyId: Long("7030059919575351301")
    }
  },
  operationTime: Timestamp({ t: 1650898981, i: 1 })
}
`````
실행 된 내용을 확인 하면 CollScan 즉, Collection에 대한 전체 스캔이 진행 되었다는 것을 확인 할 수 있다.
Sort 를 추가하면

`````
 db.movies.find({rated:"UNRATED"}).sort({runtime:1}).explain()
{
  explainVersion: '1',
  queryPlanner: {
    namespace: 'sample_mflix.movies',
    indexFilterSet: false,
    parsedQuery: { rated: { '$eq': 'UNRATED' } },
    maxIndexedOrSolutionsReached: false,
    maxIndexedAndSolutionsReached: false,
    maxScansToExplodeReached: false,
    winningPlan: {
      stage: 'SORT',
      sortPattern: { runtime: 1 },
      memLimit: 104857600,
      type: 'simple',
      inputStage: {
        stage: 'COLLSCAN',
        filter: { rated: { '$eq': 'UNRATED' } },
        direction: 'forward'
      }
    },
    rejectedPlans: []
  },
  command: {
    find: 'movies',
    filter: { rated: 'UNRATED' },
    sort: { runtime: 1 },
    '$db': 'sample_mflix'
  },
  serverInfo: {
    host: 'atlas-a1hth3-shard-00-02.5qjlg.mongodb.net',
    port: 27017,
    version: '5.0.7',
    gitVersion: 'b977129dc70eed766cbee7e412d901ee213acbda'
  },
  serverParameters: {
    internalQueryFacetBufferSizeBytes: 104857600,
    internalQueryFacetMaxOutputDocSizeBytes: 104857600,
    internalLookupStageIntermediateDocumentMaxSizeBytes: 104857600,
    internalDocumentSourceGroupMaxMemoryBytes: 104857600,
    internalQueryMaxBlockingSortMemoryUsageBytes: 104857600,
    internalQueryProhibitBlockingMergeOnMongoS: 0,
    internalQueryMaxAddToSetBytes: 104857600,
    internalDocumentSourceSetWindowFieldsMaxMemoryBytes: 104857600
  },
  ok: 1,
  '$clusterTime': {
    clusterTime: Timestamp({ t: 1650899141, i: 1 }),
    signature: {
      hash: Binary(Buffer.from("c98af7c2bbfb91882e0d77cf4dd0717b239d4ed0", "hex"), 0),
      keyId: Long("7030059919575351301")
    }
  },
  operationTime: Timestamp({ t: 1650899141, i: 1 })
}
`````
Sort 스테이지가 추가 된 것을 확인 할 수 있다.

상세한 내용을 확인 하기 위해서 executeStats 로 보면 

`````
db.movies.find({rated:"UNRATED"}).sort({runtime:1}).explain('executionStats')
{
  explainVersion: '1',
  queryPlanner: {
    namespace: 'sample_mflix.movies',
    indexFilterSet: false,
    parsedQuery: { rated: { '$eq': 'UNRATED' } },
    maxIndexedOrSolutionsReached: false,
    maxIndexedAndSolutionsReached: false,
    maxScansToExplodeReached: false,
    winningPlan: {
      stage: 'SORT',
      sortPattern: { runtime: 1 },
      memLimit: 104857600,
      type: 'simple',
      inputStage: {
        stage: 'COLLSCAN',
        filter: { rated: { '$eq': 'UNRATED' } },
        direction: 'forward'
      }
    },
    rejectedPlans: []
  },
  executionStats: {
    executionSuccess: true,
    nReturned: 751,
    executionTimeMillis: 22,
    totalKeysExamined: 0,
    totalDocsExamined: 23530,
    executionStages: {
      stage: 'SORT',
      nReturned: 751,
      executionTimeMillisEstimate: 4,
      works: 24284,
      advanced: 751,
      needTime: 23532,
      needYield: 0,
      saveState: 24,
      restoreState: 24,
      isEOF: 1,
      sortPattern: { runtime: 1 },
      memLimit: 104857600,
      type: 'simple',
      totalDataSizeSorted: 1300227,
      usedDisk: false,
      inputStage: {
        stage: 'COLLSCAN',
        filter: { rated: { '$eq': 'UNRATED' } },
        nReturned: 751,
        executionTimeMillisEstimate: 3,
        works: 23532,
        advanced: 751,
        needTime: 22780,
        needYield: 0,
        saveState: 24,
        restoreState: 24,
        isEOF: 1,
        direction: 'forward',
        docsExamined: 23530
      }
    }
  },
  command: {
    find: 'movies',
    filter: { rated: 'UNRATED' },
    sort: { runtime: 1 },
    '$db': 'sample_mflix'
  },
  serverInfo: {
    host: 'atlas-a1hth3-shard-00-02.5qjlg.mongodb.net',
    port: 27017,
    version: '5.0.7',
    gitVersion: 'b977129dc70eed766cbee7e412d901ee213acbda'
  },
  serverParameters: {
    internalQueryFacetBufferSizeBytes: 104857600,
    internalQueryFacetMaxOutputDocSizeBytes: 104857600,
    internalLookupStageIntermediateDocumentMaxSizeBytes: 104857600,
    internalDocumentSourceGroupMaxMemoryBytes: 104857600,
    internalQueryMaxBlockingSortMemoryUsageBytes: 104857600,
    internalQueryProhibitBlockingMergeOnMongoS: 0,
    internalQueryMaxAddToSetBytes: 104857600,
    internalDocumentSourceSetWindowFieldsMaxMemoryBytes: 104857600
  },
  ok: 1,
  '$clusterTime': {
    clusterTime: Timestamp({ t: 1650899329, i: 17 }),
    signature: {
      hash: Binary(Buffer.from("078941e387837628f1cd208db19f4003204f4952", "hex"), 0),
      keyId: Long("7030059919575351301")
    }
  },
  operationTime: Timestamp({ t: 1650899329, i: 17 })
}
`````
ExecutionStats 에서 반환되는 문서의 건수 751건(nReturened)를 확인 할 수 있으며 소요 시간이 22ms 이고 totalKeysExamined가 0 즉 인덱스를 이용한 검색이 없고 totalDocsExamined 즉, 읽은 문서의 개수가 23530건인 것을 볼 수 있다. executeStages에서 각 스테이지(SORT, COLLSCAN)에서 소용된 시간 및 문서 건수를 확인 할 수 있다.


#### Query 의 실행 (Index Scan)

다음과 같이 인덱스를 생성 하고 실행 하여 본다.
`````
 db.movies.createIndex({rated:1})
 
 db.movies.find({rated:"UNRATED"}).sort({runtime:1}).explain('executionStats')
{
  explainVersion: '1',
  queryPlanner: {
    namespace: 'sample_mflix.movies',
    indexFilterSet: false,
    parsedQuery: { rated: { '$eq': 'UNRATED' } },
    maxIndexedOrSolutionsReached: false,
    maxIndexedAndSolutionsReached: false,
    maxScansToExplodeReached: false,
    winningPlan: {
      stage: 'SORT',
      sortPattern: { runtime: 1 },
      memLimit: 104857600,
      type: 'simple',
      inputStage: {
        stage: 'FETCH',
        inputStage: {
          stage: 'IXSCAN',
          keyPattern: { rated: 1 },
          indexName: 'rated_1',
          isMultiKey: false,
          multiKeyPaths: { rated: [] },
          isUnique: false,
          isSparse: false,
          isPartial: false,
          indexVersion: 2,
          direction: 'forward',
          indexBounds: { rated: [ '["UNRATED", "UNRATED"]' ] }
        }
      }
    },
    rejectedPlans: []
  },
  executionStats: {
    executionSuccess: true,
    nReturned: 751,
    executionTimeMillis: 4,
    totalKeysExamined: 751,
    totalDocsExamined: 751,
    executionStages: {
      stage: 'SORT',
      nReturned: 751,
      executionTimeMillisEstimate: 0,
      works: 1504,
      advanced: 751,
      needTime: 752,
      needYield: 0,
      saveState: 1,
      restoreState: 1,
      isEOF: 1,
      sortPattern: { runtime: 1 },
      memLimit: 104857600,
      type: 'simple',
      totalDataSizeSorted: 1300227,
      usedDisk: false,
      inputStage: {
        stage: 'FETCH',
        nReturned: 751,
        executionTimeMillisEstimate: 0,
        works: 752,
        advanced: 751,
        needTime: 0,
        needYield: 0,
        saveState: 1,
        restoreState: 1,
        isEOF: 1,
        docsExamined: 751,
        alreadyHasObj: 0,
        inputStage: {
          stage: 'IXSCAN',
          nReturned: 751,
          executionTimeMillisEstimate: 0,
          works: 752,
          advanced: 751,
          needTime: 0,
          needYield: 0,
          saveState: 1,
          restoreState: 1,
          isEOF: 1,
          keyPattern: { rated: 1 },
          indexName: 'rated_1',
          isMultiKey: false,
          multiKeyPaths: { rated: [] },
          isUnique: false,
          isSparse: false,
          isPartial: false,
          indexVersion: 2,
          direction: 'forward',
          indexBounds: { rated: [ '["UNRATED", "UNRATED"]' ] },
          keysExamined: 751,
          seeks: 1,
          dupsTested: 0,
          dupsDropped: 0
        }
      }
    }
  },
  command: {
    find: 'movies',
    filter: { rated: 'UNRATED' },
    sort: { runtime: 1 },
    '$db': 'sample_mflix'
  },
  serverInfo: {
    host: 'atlas-a1hth3-shard-00-02.5qjlg.mongodb.net',
    port: 27017,
    version: '5.0.7',
    gitVersion: 'b977129dc70eed766cbee7e412d901ee213acbda'
  },
  serverParameters: {
    internalQueryFacetBufferSizeBytes: 104857600,
    internalQueryFacetMaxOutputDocSizeBytes: 104857600,
    internalLookupStageIntermediateDocumentMaxSizeBytes: 104857600,
    internalDocumentSourceGroupMaxMemoryBytes: 104857600,
    internalQueryMaxBlockingSortMemoryUsageBytes: 104857600,
    internalQueryProhibitBlockingMergeOnMongoS: 0,
    internalQueryMaxAddToSetBytes: 104857600,
    internalDocumentSourceSetWindowFieldsMaxMemoryBytes: 104857600
  },
  ok: 1,
  '$clusterTime': {
    clusterTime: Timestamp({ t: 1650900561, i: 1 }),
    signature: {
      hash: Binary(Buffer.from("5d539aaac3a4615113e8c29e0e3ed172baba04f5", "hex"), 0),
      keyId: Long("7030059919575351301")
    }
  },
  operationTime: Timestamp({ t: 1650900561, i: 1 })
}
`````

winning stage가 SORT -> FETCH -> IXSCAN으로 변경된 것을 볼 수 있다. 즉 인덱스를 이용한 검색과 이를 기반으로 데이테에 대한 FETCH가 실행 된 것이다.
executeStats도 실행 시간이 4ms로 단축 되었고 totalKeyExamined가 인덱스를 이용하였기 때문에 0에서 751로 변경되었고 중요한 지표 인 totalDocsExamined가 751로 줄어든 것을 확인 할 수 있다. executionStages의 works값을 보면 IXSCAN이 752회 수행 되었고 FETCH는 인덱스가 수행 된 횟수만을 실행된 것을 볼 수 있다. 상위의 SORT의 경우 FETCH 752회를 포함하여 1504회 즉 SORT 과정에서 752회의 work가 실행 된 것을 볼 수 있다.   

다음과 같이 인덱스를 추가 하여 본다.

`````
db.movies.createIndex({rated:1,runtime:1})

db.movies.find({rated:"UNRATED"}).sort({runtime:1}).explain('executionStats')
{
  explainVersion: '1',
  queryPlanner: {
    namespace: 'sample_mflix.movies',
    indexFilterSet: false,
    parsedQuery: { rated: { '$eq': 'UNRATED' } },
    maxIndexedOrSolutionsReached: false,
    maxIndexedAndSolutionsReached: false,
    maxScansToExplodeReached: false,
    winningPlan: {
      stage: 'FETCH',
      inputStage: {
        stage: 'IXSCAN',
        keyPattern: { rated: 1, runtime: 1 },
        indexName: 'rated_1_runtime_1',
        isMultiKey: false,
        multiKeyPaths: { rated: [], runtime: [] },
        isUnique: false,
        isSparse: false,
        isPartial: false,
        indexVersion: 2,
        direction: 'forward',
        indexBounds: {
          rated: [ '["UNRATED", "UNRATED"]' ],
          runtime: [ '[MinKey, MaxKey]' ]
        }
      }
    },
    rejectedPlans: [
      {
        stage: 'SORT',
        sortPattern: { runtime: 1 },
        memLimit: 104857600,
        type: 'simple',
        inputStage: {
          stage: 'FETCH',
          inputStage: {
            stage: 'IXSCAN',
            keyPattern: { rated: 1 },
            indexName: 'rated_1',
            isMultiKey: false,
            multiKeyPaths: { rated: [] },
            isUnique: false,
            isSparse: false,
            isPartial: false,
            indexVersion: 2,
            direction: 'forward',
            indexBounds: { rated: [ '["UNRATED", "UNRATED"]' ] }
          }
        }
      }
    ]
  },
  executionStats: {
    executionSuccess: true,
    nReturned: 751,
    executionTimeMillis: 5,
    totalKeysExamined: 751,
    totalDocsExamined: 751,
    executionStages: {
      stage: 'FETCH',
      nReturned: 751,
      executionTimeMillisEstimate: 0,
      works: 752,
      advanced: 751,
      needTime: 0,
      needYield: 0,
      saveState: 0,
      restoreState: 0,
      isEOF: 1,
      docsExamined: 751,
      alreadyHasObj: 0,
      inputStage: {
        stage: 'IXSCAN',
        nReturned: 751,
        executionTimeMillisEstimate: 0,
        works: 752,
        advanced: 751,
        needTime: 0,
        needYield: 0,
        saveState: 0,
        restoreState: 0,
        isEOF: 1,
        keyPattern: { rated: 1, runtime: 1 },
        indexName: 'rated_1_runtime_1',
        isMultiKey: false,
        multiKeyPaths: { rated: [], runtime: [] },
        isUnique: false,
        isSparse: false,
        isPartial: false,
        indexVersion: 2,
        direction: 'forward',
        indexBounds: {
          rated: [ '["UNRATED", "UNRATED"]' ],
          runtime: [ '[MinKey, MaxKey]' ]
        },
        keysExamined: 751,
        seeks: 1,
        dupsTested: 0,
        dupsDropped: 0
      }
    }
  },
  command: {
    find: 'movies',
    filter: { rated: 'UNRATED' },
    sort: { runtime: 1 },
    '$db': 'sample_mflix'
  },
  serverInfo: {
    host: 'atlas-a1hth3-shard-00-02.5qjlg.mongodb.net',
    port: 27017,
    version: '5.0.7',
    gitVersion: 'b977129dc70eed766cbee7e412d901ee213acbda'
  },
  serverParameters: {
    internalQueryFacetBufferSizeBytes: 104857600,
    internalQueryFacetMaxOutputDocSizeBytes: 104857600,
    internalLookupStageIntermediateDocumentMaxSizeBytes: 104857600,
    internalDocumentSourceGroupMaxMemoryBytes: 104857600,
    internalQueryMaxBlockingSortMemoryUsageBytes: 104857600,
    internalQueryProhibitBlockingMergeOnMongoS: 0,
    internalQueryMaxAddToSetBytes: 104857600,
    internalDocumentSourceSetWindowFieldsMaxMemoryBytes: 104857600
  },
  ok: 1,
  '$clusterTime': {
    clusterTime: Timestamp({ t: 1650901009, i: 1 }),
    signature: {
      hash: Binary(Buffer.from("a3ee559b890374866071357082f51622f8d70dc3", "hex"), 0),
      keyId: Long("7030059919575351301")
    }
  },
  operationTime: Timestamp({ t: 1650901009, i: 1 })
}
`````

인덱스에 정렬을 위한 부분이 추가 되었기 때문에 executionStats에 SORT가 제외 되었고 사용한 인덱스 이름이 rated_1_runtime_1이 사용 된 것을 볼 수 있다. work수가 기존 1504dptj 752로 줄어 든 것을 볼 수 있다. (즉, 인덱스를 이용한 정렬이 이용되어 SORT 작업이 생략 된 것)
Reject plan 에서 rated_1을 이용한 검색 (SORT -> FETCH -> IXSCAN)이 취소 된 것을 볼 수 있다

#### Plan Cache
쿼리가 실행 되면 실행 되는 계획은 캐시에 저장 된다. 그 개수는 internalQueryCacheSize 에서 저장 가능한 QueryPlan의 개수를 볼 수 있다. 

`````
admin> db.runCommand({getParameter:1,"internalQueryCacheSize":1})
{
  internalQueryCacheSize: 5000,
  ok: 1,
  '$clusterTime': {
    clusterTime: Timestamp({ t: 1650941672, i: 1 }),
    signature: {
      hash: Binary(Buffer.from("1963344f3d3457555567831317150277e051198c", "hex"), 0),
      keyId: Long("7030059919575351301")
    }
  },
  operationTime: Timestamp({ t: 1650941672, i: 1 })
}
`````
저장 된 플랜은 해당 컬렉션의 인덱스가 변경 되지 않는 다면 저장 되며 개수가 넘어가면 오래된 것 부터 삭제 된다.
해당 내용을 확인 해 볼 수 있다.

`````
sample_mflix> db.movies.getPlanCache().list()
[
  {
    queryHash: '96439EE1',
    planCacheKey: 'D0C6C30C',
    isActive: false,
    works: Long("101"),
    timeOfCreation: ISODate("2022-04-26T02:57:11.885Z"),
    createdFromQuery: {
      query: { rated: 'UNRATED' },
      sort: { runtime: 1 },
      projection: {}
    },
    cachedPlan: {
      stage: 'FETCH',
      inputStage: {
        stage: 'IXSCAN',
        keyPattern: { rated: 1, runtime: 1 },
        indexName: 'rated_1_runtime_1',
        isMultiKey: false,
        multiKeyPaths: { rated: [], runtime: [] },
        isUnique: false,
        isSparse: false,
        isPartial: false,
        indexVersion: 2,
        direction: 'forward',
        indexBounds: {
          rated: [ '["UNRATED", "UNRATED"]' ],
          runtime: [ '[MinKey, MaxKey]' ]
        }
      }
    },
..
`````