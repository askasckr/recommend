# recommend
A simple portfolio recommendation tool ``` RecsApplication ```.

The following libraries are used:
```
h2 embedded database
project lombok
guava libs (RateLimiter)
apache commons
hibernate
springboot 2.x
Mockito
SpringBootTest
```
### Assumptions:
-----------------

A new ```LOOKUP.INVESTMENT_CATEGORY``` or ```LOOKUP.INVESTMENT_RISK```  record can be added to the database independent of the records ```RECOMMEND.PREDEFINED_PORTFOLIO```  table, but a validation takes place during the updates or save into ```RECOMMEND.PREDEFINED_PORTFOLIO```  table to make sure the percents sums up to 100.

**Note:** All the requests expect Client-Id in header to distinguish the clients. More than two concurrent client requests result in HTTP 429 Too Many Requests response status code.

The below seed data is populated at app startup (look at ``` SeedDataService ```):
```
String[] investmentCategories = {"Bonds", "Large Cap", "Mid Cap", "Foreign", "Small Cap"};

List<Integer> InvesmentRiskLevelSeed() {
    return Stream.iterate(1, n -> n + 1).limit(10).collect(toList());
}

private List<?>[] getInvestmentRiskCategoryPercentMatrixSeed() {
	List<?>[] riskCategoryPercentMatrix = {
		Arrays.asList(80, 20, 0, 0, 0),
		Arrays.asList(70, 15, 15, 0, 0),
		Arrays.asList(60, 15, 15, 10, 0),
		Arrays.asList(50, 20, 20, 10, 0),
		Arrays.asList(40, 20, 20, 20, 0),
		Arrays.asList(35, 25, 5, 30, 5),
		Arrays.asList(20, 25, 25, 25, 5),
		Arrays.asList(10, 20, 40, 20, 10),
		Arrays.asList(5, 15, 40, 25, 15),
		Arrays.asList(0, 5, 25, 30, 40)
	};
	return riskCategoryPercentMatrix;
}
```
Included: Features like ```Audit Fields, Logging, Global Exception Handler, RateLimiter, Restriction to allow up to 2 concurrent unique clients etc```
 
Not Included: ```Versioning, Endpoints to Investment Risk, Investment Category, AppClient (I wish I'd included them too)```

### Testcases:
--------------

Testcases are written for test endpoints, services, validators and repositories concurrent clients, rebalancing, save predefined portfolios etc.

+![Imgur](https://github.com/askasckr/recommend/blob/master/TestCoverageScreenShot.png)
+

Here is the github repo: https://github.com/askasckr/recommend

### Clone from it:
-----------------
```
git clone https://github.com/askasckr/recommend
cd recommend
git pull 
```

### Build it and run tests:
--------------------------
```
mvn clean install
```
Test Results:

[INFO] 

[INFO] Tests run: 35, Failures: 0, Errors: 0, Skipped: 0

Note: A jar file gets created at ./target/recs-0.0.1-SNAPSHOT.jar

### Run it:
-------
```
java -Dspring.profiles.active=local -jar ./target/recs-0.0.1-SNAPSHOT.jar

mvn spring-boot:run -Dspring-boot.run.profiles=local
```
### Endpoints: (for live api end points are avialable here https://intense-oasis-48244.herokuapp.com )
------------
#### Note: All the requests expect Client-Id in header to distinguish the clients and is used in ```RecsClientRateLimitInterceptor``` to control concurrent requests on two unique clients. More than two concurrent client requests result in HTTP 429 Too Many Requests response status code.

#### 1. To get all the predefined portfolios as list(returns just a raw list of all predefined portfolio percents, UI might need to make group by on investmentRisk.id):
**Note:** Make sure to add Client-Id in header.

GET https://intense-oasis-48244.herokuapp.com/api/v1/predefined/portfolios


Check the curl request below:
```
curl -X GET \
  http://localhost:8080/api/v1/predefined/portfolios \
  -H 'Cache-Control: no-cache' \
  -H 'Client-Id: test'
```
Response:

```
[
    {
        "id": 1,
        "investmentCategory": {
            "id": 2,
            "name": "Bonds",
            "displayOrder": 0,
            "info": null
        },
        "investmentRisk": {
            "id": 1,
            "level": 1,
            "info": null
        },
        "percent": 80
    },
    {
        "id": 2,
        "investmentCategory": {
            "id": 3,
            "name": "Large Cap",
            "displayOrder": 1,
            "info": null
        },
        "investmentRisk": {
            "id": 1,
            "level": 1,
            "info": null
        },
        "percent": 20
    },
    {
        "id": 3,
        "investmentCategory": {
            "id": 4,
            "name": "Mid Cap",
            "displayOrder": 2,
            "info": null
        },
        "investmentRisk": {
            "id": 1,
            "level": 1,
            "info": null
        },
        "percent": 0
    },
    {
        "id": 4,
        "investmentCategory": {
            "id": 5,
            "name": "Foreign",
            "displayOrder": 3,
            "info": null
        },
        "investmentRisk": {
            "id": 1,
            "level": 1,
            "info": null
        },
        "percent": 0
    },
    {
        "id": 5,
        "investmentCategory": {
            "id": 6,
            "name": "Small Cap",
            "displayOrder": 4,
            "info": null
        },
        "investmentRisk": {
            "id": 1,
            "level": 1,
            "info": null
        },
        "percent": 0
    },
    {
        "id": 6,
        "investmentCategory": {
            "id": 2,
            "name": "Bonds",
            "displayOrder": 0,
            "info": null
        },
        "investmentRisk": {
            "id": 2,
            "level": 2,
            "info": null
        },
        "percent": 70
    },
    {
        "id": 7,
        "investmentCategory": {
            "id": 3,
            "name": "Large Cap",
            "displayOrder": 1,
            "info": null
        },
        "investmentRisk": {
            "id": 2,
            "level": 2,
            "info": null
        },
        "percent": 15
    },
    {
        "id": 8,
        "investmentCategory": {
            "id": 4,
            "name": "Mid Cap",
            "displayOrder": 2,
            "info": null
        },
        "investmentRisk": {
            "id": 2,
            "level": 2,
            "info": null
        },
        "percent": 15
    },
    {
        "id": 9,
        "investmentCategory": {
            "id": 5,
            "name": "Foreign",
            "displayOrder": 3,
            "info": null
        },
        "investmentRisk": {
            "id": 2,
            "level": 2,
            "info": null
        },
        "percent": 0
    },
    {
        "id": 10,
        "investmentCategory": {
            "id": 6,
            "name": "Small Cap",
            "displayOrder": 4,
            "info": null
        },
        "investmentRisk": {
            "id": 2,
            "level": 2,
            "info": null
        },
        "percent": 0
    },
    {
        "id": 11,
        "investmentCategory": {
            "id": 2,
            "name": "Bonds",
            "displayOrder": 0,
            "info": null
        },
        "investmentRisk": {
            "id": 3,
            "level": 3,
            "info": null
        },
        "percent": 60
    },
    {
        "id": 12,
        "investmentCategory": {
            "id": 3,
            "name": "Large Cap",
            "displayOrder": 1,
            "info": null
        },
        "investmentRisk": {
            "id": 3,
            "level": 3,
            "info": null
        },
        "percent": 15
    },
    {
        "id": 13,
        "investmentCategory": {
            "id": 4,
            "name": "Mid Cap",
            "displayOrder": 2,
            "info": null
        },
        "investmentRisk": {
            "id": 3,
            "level": 3,
            "info": null
        },
        "percent": 15
    },
    {
        "id": 14,
        "investmentCategory": {
            "id": 5,
            "name": "Foreign",
            "displayOrder": 3,
            "info": null
        },
        "investmentRisk": {
            "id": 3,
            "level": 3,
            "info": null
        },
        "percent": 10
    },
    {
        "id": 15,
        "investmentCategory": {
            "id": 6,
            "name": "Small Cap",
            "displayOrder": 4,
            "info": null
        },
        "investmentRisk": {
            "id": 3,
            "level": 3,
            "info": null
        },
        "percent": 0
    },
    {
        "id": 16,
        "investmentCategory": {
            "id": 2,
            "name": "Bonds",
            "displayOrder": 0,
            "info": null
        },
        "investmentRisk": {
            "id": 4,
            "level": 4,
            "info": null
        },
        "percent": 50
    },
    {
        "id": 17,
        "investmentCategory": {
            "id": 3,
            "name": "Large Cap",
            "displayOrder": 1,
            "info": null
        },
        "investmentRisk": {
            "id": 4,
            "level": 4,
            "info": null
        },
        "percent": 20
    },
    {
        "id": 18,
        "investmentCategory": {
            "id": 4,
            "name": "Mid Cap",
            "displayOrder": 2,
            "info": null
        },
        "investmentRisk": {
            "id": 4,
            "level": 4,
            "info": null
        },
        "percent": 20
    },
    {
        "id": 19,
        "investmentCategory": {
            "id": 5,
            "name": "Foreign",
            "displayOrder": 3,
            "info": null
        },
        "investmentRisk": {
            "id": 4,
            "level": 4,
            "info": null
        },
        "percent": 10
    },
    {
        "id": 20,
        "investmentCategory": {
            "id": 6,
            "name": "Small Cap",
            "displayOrder": 4,
            "info": null
        },
        "investmentRisk": {
            "id": 4,
            "level": 4,
            "info": null
        },
        "percent": 0
    },
    {
        "id": 21,
        "investmentCategory": {
            "id": 2,
            "name": "Bonds",
            "displayOrder": 0,
            "info": null
        },
        "investmentRisk": {
            "id": 5,
            "level": 5,
            "info": null
        },
        "percent": 40
    },
    {
        "id": 22,
        "investmentCategory": {
            "id": 3,
            "name": "Large Cap",
            "displayOrder": 1,
            "info": null
        },
        "investmentRisk": {
            "id": 5,
            "level": 5,
            "info": null
        },
        "percent": 20
    },
    {
        "id": 23,
        "investmentCategory": {
            "id": 4,
            "name": "Mid Cap",
            "displayOrder": 2,
            "info": null
        },
        "investmentRisk": {
            "id": 5,
            "level": 5,
            "info": null
        },
        "percent": 20
    },
    {
        "id": 24,
        "investmentCategory": {
            "id": 5,
            "name": "Foreign",
            "displayOrder": 3,
            "info": null
        },
        "investmentRisk": {
            "id": 5,
            "level": 5,
            "info": null
        },
        "percent": 20
    },
    {
        "id": 25,
        "investmentCategory": {
            "id": 6,
            "name": "Small Cap",
            "displayOrder": 4,
            "info": null
        },
        "investmentRisk": {
            "id": 5,
            "level": 5,
            "info": null
        },
        "percent": 0
    },
    {
        "id": 26,
        "investmentCategory": {
            "id": 2,
            "name": "Bonds",
            "displayOrder": 0,
            "info": null
        },
        "investmentRisk": {
            "id": 6,
            "level": 6,
            "info": null
        },
        "percent": 35
    },
    {
        "id": 27,
        "investmentCategory": {
            "id": 3,
            "name": "Large Cap",
            "displayOrder": 1,
            "info": null
        },
        "investmentRisk": {
            "id": 6,
            "level": 6,
            "info": null
        },
        "percent": 25
    },
    {
        "id": 28,
        "investmentCategory": {
            "id": 4,
            "name": "Mid Cap",
            "displayOrder": 2,
            "info": null
        },
        "investmentRisk": {
            "id": 6,
            "level": 6,
            "info": null
        },
        "percent": 5
    },
    {
        "id": 29,
        "investmentCategory": {
            "id": 5,
            "name": "Foreign",
            "displayOrder": 3,
            "info": null
        },
        "investmentRisk": {
            "id": 6,
            "level": 6,
            "info": null
        },
        "percent": 30
    },
    {
        "id": 30,
        "investmentCategory": {
            "id": 6,
            "name": "Small Cap",
            "displayOrder": 4,
            "info": null
        },
        "investmentRisk": {
            "id": 6,
            "level": 6,
            "info": null
        },
        "percent": 5
    },
    {
        "id": 31,
        "investmentCategory": {
            "id": 2,
            "name": "Bonds",
            "displayOrder": 0,
            "info": null
        },
        "investmentRisk": {
            "id": 7,
            "level": 7,
            "info": null
        },
        "percent": 20
    },
    {
        "id": 32,
        "investmentCategory": {
            "id": 3,
            "name": "Large Cap",
            "displayOrder": 1,
            "info": null
        },
        "investmentRisk": {
            "id": 7,
            "level": 7,
            "info": null
        },
        "percent": 25
    },
    {
        "id": 33,
        "investmentCategory": {
            "id": 4,
            "name": "Mid Cap",
            "displayOrder": 2,
            "info": null
        },
        "investmentRisk": {
            "id": 7,
            "level": 7,
            "info": null
        },
        "percent": 25
    },
    {
        "id": 34,
        "investmentCategory": {
            "id": 5,
            "name": "Foreign",
            "displayOrder": 3,
            "info": null
        },
        "investmentRisk": {
            "id": 7,
            "level": 7,
            "info": null
        },
        "percent": 25
    },
    {
        "id": 35,
        "investmentCategory": {
            "id": 6,
            "name": "Small Cap",
            "displayOrder": 4,
            "info": null
        },
        "investmentRisk": {
            "id": 7,
            "level": 7,
            "info": null
        },
        "percent": 5
    },
    {
        "id": 36,
        "investmentCategory": {
            "id": 2,
            "name": "Bonds",
            "displayOrder": 0,
            "info": null
        },
        "investmentRisk": {
            "id": 8,
            "level": 8,
            "info": null
        },
        "percent": 10
    },
    {
        "id": 37,
        "investmentCategory": {
            "id": 3,
            "name": "Large Cap",
            "displayOrder": 1,
            "info": null
        },
        "investmentRisk": {
            "id": 8,
            "level": 8,
            "info": null
        },
        "percent": 20
    },
    {
        "id": 38,
        "investmentCategory": {
            "id": 4,
            "name": "Mid Cap",
            "displayOrder": 2,
            "info": null
        },
        "investmentRisk": {
            "id": 8,
            "level": 8,
            "info": null
        },
        "percent": 40
    },
    {
        "id": 39,
        "investmentCategory": {
            "id": 5,
            "name": "Foreign",
            "displayOrder": 3,
            "info": null
        },
        "investmentRisk": {
            "id": 8,
            "level": 8,
            "info": null
        },
        "percent": 20
    },
    {
        "id": 40,
        "investmentCategory": {
            "id": 6,
            "name": "Small Cap",
            "displayOrder": 4,
            "info": null
        },
        "investmentRisk": {
            "id": 8,
            "level": 8,
            "info": null
        },
        "percent": 10
    },
    {
        "id": 41,
        "investmentCategory": {
            "id": 2,
            "name": "Bonds",
            "displayOrder": 0,
            "info": null
        },
        "investmentRisk": {
            "id": 9,
            "level": 9,
            "info": null
        },
        "percent": 5
    },
    {
        "id": 42,
        "investmentCategory": {
            "id": 3,
            "name": "Large Cap",
            "displayOrder": 1,
            "info": null
        },
        "investmentRisk": {
            "id": 9,
            "level": 9,
            "info": null
        },
        "percent": 15
    },
    {
        "id": 43,
        "investmentCategory": {
            "id": 4,
            "name": "Mid Cap",
            "displayOrder": 2,
            "info": null
        },
        "investmentRisk": {
            "id": 9,
            "level": 9,
            "info": null
        },
        "percent": 40
    },
    {
        "id": 44,
        "investmentCategory": {
            "id": 5,
            "name": "Foreign",
            "displayOrder": 3,
            "info": null
        },
        "investmentRisk": {
            "id": 9,
            "level": 9,
            "info": null
        },
        "percent": 25
    },
    {
        "id": 45,
        "investmentCategory": {
            "id": 6,
            "name": "Small Cap",
            "displayOrder": 4,
            "info": null
        },
        "investmentRisk": {
            "id": 9,
            "level": 9,
            "info": null
        },
        "percent": 15
    },
    {
        "id": 46,
        "investmentCategory": {
            "id": 2,
            "name": "Bonds",
            "displayOrder": 0,
            "info": null
        },
        "investmentRisk": {
            "id": 10,
            "level": 10,
            "info": null
        },
        "percent": 0
    },
    {
        "id": 47,
        "investmentCategory": {
            "id": 3,
            "name": "Large Cap",
            "displayOrder": 1,
            "info": null
        },
        "investmentRisk": {
            "id": 10,
            "level": 10,
            "info": null
        },
        "percent": 5
    },
    {
        "id": 48,
        "investmentCategory": {
            "id": 4,
            "name": "Mid Cap",
            "displayOrder": 2,
            "info": null
        },
        "investmentRisk": {
            "id": 10,
            "level": 10,
            "info": null
        },
        "percent": 25
    },
    {
        "id": 49,
        "investmentCategory": {
            "id": 5,
            "name": "Foreign",
            "displayOrder": 3,
            "info": null
        },
        "investmentRisk": {
            "id": 10,
            "level": 10,
            "info": null
        },
        "percent": 30
    },
    {
        "id": 50,
        "investmentCategory": {
            "id": 6,
            "name": "Small Cap",
            "displayOrder": 4,
            "info": null
        },
        "investmentRisk": {
            "id": 10,
            "level": 10,
            "info": null
        },
        "percent": 40
    }
]

```

#### 2. To get all the predefined portfolios as matrix/map (alternatively, this helps if UI prefers for an easy read):
**Note:** Make sure to add Client-Id in header.

GET https://intense-oasis-48244.herokuapp.com/api/v1/predefined/portfolios/matrix

Check the curl request below:
```
curl -X GET \
  http://localhost:8080/api/v1/predefined/portfolios/matrix \
  -H 'Cache-Control: no-cache' \
  -H 'Client-Id: test1'
```
Response:
```
{
    "1": [
        {
            "id": 1,
            "investmentCategory": {
                "id": 2,
                "name": "Bonds",
                "displayOrder": 0,
                "info": null
            },
            "investmentRisk": {
                "id": 1,
                "level": 1,
                "info": null
            },
            "percent": 80
        },
        {
            "id": 2,
            "investmentCategory": {
                "id": 3,
                "name": "Large Cap",
                "displayOrder": 1,
                "info": null
            },
            "investmentRisk": {
                "id": 1,
                "level": 1,
                "info": null
            },
            "percent": 20
        },
        {
            "id": 3,
            "investmentCategory": {
                "id": 4,
                "name": "Mid Cap",
                "displayOrder": 2,
                "info": null
            },
            "investmentRisk": {
                "id": 1,
                "level": 1,
                "info": null
            },
            "percent": 0
        },
        {
            "id": 4,
            "investmentCategory": {
                "id": 5,
                "name": "Foreign",
                "displayOrder": 3,
                "info": null
            },
            "investmentRisk": {
                "id": 1,
                "level": 1,
                "info": null
            },
            "percent": 0
        },
        {
            "id": 5,
            "investmentCategory": {
                "id": 6,
                "name": "Small Cap",
                "displayOrder": 4,
                "info": null
            },
            "investmentRisk": {
                "id": 1,
                "level": 1,
                "info": null
            },
            "percent": 0
        }
    ],
    "2": [
        {
            "id": 6,
            "investmentCategory": {
                "id": 2,
                "name": "Bonds",
                "displayOrder": 0,
                "info": null
            },
            "investmentRisk": {
                "id": 2,
                "level": 2,
                "info": null
            },
            "percent": 70
        },
        {
            "id": 7,
            "investmentCategory": {
                "id": 3,
                "name": "Large Cap",
                "displayOrder": 1,
                "info": null
            },
            "investmentRisk": {
                "id": 2,
                "level": 2,
                "info": null
            },
            "percent": 15
        },
        {
            "id": 8,
            "investmentCategory": {
                "id": 4,
                "name": "Mid Cap",
                "displayOrder": 2,
                "info": null
            },
            "investmentRisk": {
                "id": 2,
                "level": 2,
                "info": null
            },
            "percent": 15
        },
        {
            "id": 9,
            "investmentCategory": {
                "id": 5,
                "name": "Foreign",
                "displayOrder": 3,
                "info": null
            },
            "investmentRisk": {
                "id": 2,
                "level": 2,
                "info": null
            },
            "percent": 0
        },
        {
            "id": 10,
            "investmentCategory": {
                "id": 6,
                "name": "Small Cap",
                "displayOrder": 4,
                "info": null
            },
            "investmentRisk": {
                "id": 2,
                "level": 2,
                "info": null
            },
            "percent": 0
        }
    ],
    "3": [
        {
            "id": 11,
            "investmentCategory": {
                "id": 2,
                "name": "Bonds",
                "displayOrder": 0,
                "info": null
            },
            "investmentRisk": {
                "id": 3,
                "level": 3,
                "info": null
            },
            "percent": 60
        },
        {
            "id": 12,
            "investmentCategory": {
                "id": 3,
                "name": "Large Cap",
                "displayOrder": 1,
                "info": null
            },
            "investmentRisk": {
                "id": 3,
                "level": 3,
                "info": null
            },
            "percent": 15
        },
        {
            "id": 13,
            "investmentCategory": {
                "id": 4,
                "name": "Mid Cap",
                "displayOrder": 2,
                "info": null
            },
            "investmentRisk": {
                "id": 3,
                "level": 3,
                "info": null
            },
            "percent": 15
        },
        {
            "id": 14,
            "investmentCategory": {
                "id": 5,
                "name": "Foreign",
                "displayOrder": 3,
                "info": null
            },
            "investmentRisk": {
                "id": 3,
                "level": 3,
                "info": null
            },
            "percent": 10
        },
        {
            "id": 15,
            "investmentCategory": {
                "id": 6,
                "name": "Small Cap",
                "displayOrder": 4,
                "info": null
            },
            "investmentRisk": {
                "id": 3,
                "level": 3,
                "info": null
            },
            "percent": 0
        }
    ],
    "4": [
        {
            "id": 16,
            "investmentCategory": {
                "id": 2,
                "name": "Bonds",
                "displayOrder": 0,
                "info": null
            },
            "investmentRisk": {
                "id": 4,
                "level": 4,
                "info": null
            },
            "percent": 50
        },
        {
            "id": 17,
            "investmentCategory": {
                "id": 3,
                "name": "Large Cap",
                "displayOrder": 1,
                "info": null
            },
            "investmentRisk": {
                "id": 4,
                "level": 4,
                "info": null
            },
            "percent": 20
        },
        {
            "id": 18,
            "investmentCategory": {
                "id": 4,
                "name": "Mid Cap",
                "displayOrder": 2,
                "info": null
            },
            "investmentRisk": {
                "id": 4,
                "level": 4,
                "info": null
            },
            "percent": 20
        },
        {
            "id": 19,
            "investmentCategory": {
                "id": 5,
                "name": "Foreign",
                "displayOrder": 3,
                "info": null
            },
            "investmentRisk": {
                "id": 4,
                "level": 4,
                "info": null
            },
            "percent": 10
        },
        {
            "id": 20,
            "investmentCategory": {
                "id": 6,
                "name": "Small Cap",
                "displayOrder": 4,
                "info": null
            },
            "investmentRisk": {
                "id": 4,
                "level": 4,
                "info": null
            },
            "percent": 0
        }
    ],
    "5": [
        {
            "id": 21,
            "investmentCategory": {
                "id": 2,
                "name": "Bonds",
                "displayOrder": 0,
                "info": null
            },
            "investmentRisk": {
                "id": 5,
                "level": 5,
                "info": null
            },
            "percent": 40
        },
        {
            "id": 22,
            "investmentCategory": {
                "id": 3,
                "name": "Large Cap",
                "displayOrder": 1,
                "info": null
            },
            "investmentRisk": {
                "id": 5,
                "level": 5,
                "info": null
            },
            "percent": 20
        },
        {
            "id": 23,
            "investmentCategory": {
                "id": 4,
                "name": "Mid Cap",
                "displayOrder": 2,
                "info": null
            },
            "investmentRisk": {
                "id": 5,
                "level": 5,
                "info": null
            },
            "percent": 20
        },
        {
            "id": 24,
            "investmentCategory": {
                "id": 5,
                "name": "Foreign",
                "displayOrder": 3,
                "info": null
            },
            "investmentRisk": {
                "id": 5,
                "level": 5,
                "info": null
            },
            "percent": 20
        },
        {
            "id": 25,
            "investmentCategory": {
                "id": 6,
                "name": "Small Cap",
                "displayOrder": 4,
                "info": null
            },
            "investmentRisk": {
                "id": 5,
                "level": 5,
                "info": null
            },
            "percent": 0
        }
    ],
    "6": [
        {
            "id": 26,
            "investmentCategory": {
                "id": 2,
                "name": "Bonds",
                "displayOrder": 0,
                "info": null
            },
            "investmentRisk": {
                "id": 6,
                "level": 6,
                "info": null
            },
            "percent": 35
        },
        {
            "id": 27,
            "investmentCategory": {
                "id": 3,
                "name": "Large Cap",
                "displayOrder": 1,
                "info": null
            },
            "investmentRisk": {
                "id": 6,
                "level": 6,
                "info": null
            },
            "percent": 25
        },
        {
            "id": 28,
            "investmentCategory": {
                "id": 4,
                "name": "Mid Cap",
                "displayOrder": 2,
                "info": null
            },
            "investmentRisk": {
                "id": 6,
                "level": 6,
                "info": null
            },
            "percent": 5
        },
        {
            "id": 29,
            "investmentCategory": {
                "id": 5,
                "name": "Foreign",
                "displayOrder": 3,
                "info": null
            },
            "investmentRisk": {
                "id": 6,
                "level": 6,
                "info": null
            },
            "percent": 30
        },
        {
            "id": 30,
            "investmentCategory": {
                "id": 6,
                "name": "Small Cap",
                "displayOrder": 4,
                "info": null
            },
            "investmentRisk": {
                "id": 6,
                "level": 6,
                "info": null
            },
            "percent": 5
        }
    ],
    "7": [
        {
            "id": 31,
            "investmentCategory": {
                "id": 2,
                "name": "Bonds",
                "displayOrder": 0,
                "info": null
            },
            "investmentRisk": {
                "id": 7,
                "level": 7,
                "info": null
            },
            "percent": 20
        },
        {
            "id": 32,
            "investmentCategory": {
                "id": 3,
                "name": "Large Cap",
                "displayOrder": 1,
                "info": null
            },
            "investmentRisk": {
                "id": 7,
                "level": 7,
                "info": null
            },
            "percent": 25
        },
        {
            "id": 33,
            "investmentCategory": {
                "id": 4,
                "name": "Mid Cap",
                "displayOrder": 2,
                "info": null
            },
            "investmentRisk": {
                "id": 7,
                "level": 7,
                "info": null
            },
            "percent": 25
        },
        {
            "id": 34,
            "investmentCategory": {
                "id": 5,
                "name": "Foreign",
                "displayOrder": 3,
                "info": null
            },
            "investmentRisk": {
                "id": 7,
                "level": 7,
                "info": null
            },
            "percent": 25
        },
        {
            "id": 35,
            "investmentCategory": {
                "id": 6,
                "name": "Small Cap",
                "displayOrder": 4,
                "info": null
            },
            "investmentRisk": {
                "id": 7,
                "level": 7,
                "info": null
            },
            "percent": 5
        }
    ],
    "8": [
        {
            "id": 36,
            "investmentCategory": {
                "id": 2,
                "name": "Bonds",
                "displayOrder": 0,
                "info": null
            },
            "investmentRisk": {
                "id": 8,
                "level": 8,
                "info": null
            },
            "percent": 10
        },
        {
            "id": 37,
            "investmentCategory": {
                "id": 3,
                "name": "Large Cap",
                "displayOrder": 1,
                "info": null
            },
            "investmentRisk": {
                "id": 8,
                "level": 8,
                "info": null
            },
            "percent": 20
        },
        {
            "id": 38,
            "investmentCategory": {
                "id": 4,
                "name": "Mid Cap",
                "displayOrder": 2,
                "info": null
            },
            "investmentRisk": {
                "id": 8,
                "level": 8,
                "info": null
            },
            "percent": 40
        },
        {
            "id": 39,
            "investmentCategory": {
                "id": 5,
                "name": "Foreign",
                "displayOrder": 3,
                "info": null
            },
            "investmentRisk": {
                "id": 8,
                "level": 8,
                "info": null
            },
            "percent": 20
        },
        {
            "id": 40,
            "investmentCategory": {
                "id": 6,
                "name": "Small Cap",
                "displayOrder": 4,
                "info": null
            },
            "investmentRisk": {
                "id": 8,
                "level": 8,
                "info": null
            },
            "percent": 10
        }
    ],
    "9": [
        {
            "id": 41,
            "investmentCategory": {
                "id": 2,
                "name": "Bonds",
                "displayOrder": 0,
                "info": null
            },
            "investmentRisk": {
                "id": 9,
                "level": 9,
                "info": null
            },
            "percent": 5
        },
        {
            "id": 42,
            "investmentCategory": {
                "id": 3,
                "name": "Large Cap",
                "displayOrder": 1,
                "info": null
            },
            "investmentRisk": {
                "id": 9,
                "level": 9,
                "info": null
            },
            "percent": 15
        },
        {
            "id": 43,
            "investmentCategory": {
                "id": 4,
                "name": "Mid Cap",
                "displayOrder": 2,
                "info": null
            },
            "investmentRisk": {
                "id": 9,
                "level": 9,
                "info": null
            },
            "percent": 40
        },
        {
            "id": 44,
            "investmentCategory": {
                "id": 5,
                "name": "Foreign",
                "displayOrder": 3,
                "info": null
            },
            "investmentRisk": {
                "id": 9,
                "level": 9,
                "info": null
            },
            "percent": 25
        },
        {
            "id": 45,
            "investmentCategory": {
                "id": 6,
                "name": "Small Cap",
                "displayOrder": 4,
                "info": null
            },
            "investmentRisk": {
                "id": 9,
                "level": 9,
                "info": null
            },
            "percent": 15
        }
    ],
    "10": [
        {
            "id": 46,
            "investmentCategory": {
                "id": 2,
                "name": "Bonds",
                "displayOrder": 0,
                "info": null
            },
            "investmentRisk": {
                "id": 10,
                "level": 10,
                "info": null
            },
            "percent": 0
        },
        {
            "id": 47,
            "investmentCategory": {
                "id": 3,
                "name": "Large Cap",
                "displayOrder": 1,
                "info": null
            },
            "investmentRisk": {
                "id": 10,
                "level": 10,
                "info": null
            },
            "percent": 5
        },
        {
            "id": 48,
            "investmentCategory": {
                "id": 4,
                "name": "Mid Cap",
                "displayOrder": 2,
                "info": null
            },
            "investmentRisk": {
                "id": 10,
                "level": 10,
                "info": null
            },
            "percent": 25
        },
        {
            "id": 49,
            "investmentCategory": {
                "id": 5,
                "name": "Foreign",
                "displayOrder": 3,
                "info": null
            },
            "investmentRisk": {
                "id": 10,
                "level": 10,
                "info": null
            },
            "percent": 30
        },
        {
            "id": 50,
            "investmentCategory": {
                "id": 6,
                "name": "Small Cap",
                "displayOrder": 4,
                "info": null
            },
            "investmentRisk": {
                "id": 10,
                "level": 10,
                "info": null
            },
            "percent": 40
        }
    ]
}

```

#### 3. To save predefined portfolios:
**Note:** Returns 400 Bad Request if the existing and/or given percents for a given investmentRisk.id exceeds 100.

**Note:** Make sure to add Client-Id in header.

POST https://intense-oasis-48244.herokuapp.com/api/v1/predefined/portfolios

Check the curl request below:
```
curl -X POST \
  http://localhost:8080/api/v1/predefined/portfolios \
  -H 'Cache-Control: no-cache' \
  -H 'Client-Id: test3' \
  -H 'Content-Type: application/json' \
  -d '[
{
            "id": 1,
            "investmentCategory": {
                "id": 2,
                "name": "Bonds",
                "displayOrder": 0,
                "info": null
            },
            "investmentRisk": {
                "id": 1,
                "level": 1,
                "info": null
            },
            "percent": 80
        },
        {
            "id": 2,
            "investmentCategory": {
                "id": 3,
                "name": "Large Cap",
                "displayOrder": 1,
                "info": null
            },
            "investmentRisk": {
                "id": 1,
                "level": 1,
                "info": null
            },
            "percent": 20
        },
        {
            "id": 3,
            "investmentCategory": {
                "id": 4,
                "name": "Mid Cap",
                "displayOrder": 2,
                "info": null
            },
            "investmentRisk": {
                "id": 1,
                "level": 1,
                "info": null
            },
            "percent": 0
        },
        {
            "id": 4,
            "investmentCategory": {
                "id": 5,
                "name": "Foreign",
                "displayOrder": 3,
                "info": null
            },
            "investmentRisk": {
                "id": 1,
                "level": 1,
                "info": null
            },
            "percent": 0
        },
        {
            "id": 5,
            "investmentCategory": {
                "id": 6,
                "name": "Small Cap",
                "displayOrder": 4,
                "info": null
            },
            "investmentRisk": {
                "id": 1,
                "level": 1,
                "info": null
            },
            "percent": 0
        },
        {
            "id": 6,
            "investmentCategory": {
                "id": 2,
                "name": "Bonds",
                "displayOrder": 0,
                "info": null
            },
            "investmentRisk": {
                "id": 2,
                "level": 2,
                "info": null
            },
            "percent": 70
        },
        {
            "id": 7,
            "investmentCategory": {
                "id": 3,
                "name": "Large Cap",
                "displayOrder": 1,
                "info": null
            },
            "investmentRisk": {
                "id": 2,
                "level": 2,
                "info": null
            },
            "percent": 15
        },
        {
            "id": 8,
            "investmentCategory": {
                "id": 4,
                "name": "Mid Cap",
                "displayOrder": 2,
                "info": null
            },
            "investmentRisk": {
                "id": 2,
                "level": 2,
                "info": null
            },
            "percent": 15
        }
        ]'
```

#### 4. To rebalance the customer allocations using predefined portfolio:
**Note:** Returns 404 Bad Request if one or more amounts are negative or sum is not positive number.

**Note:** Make sure to add Client-Id in header.

POST https://intense-oasis-48244.herokuapp.com/api/v1/predefined/portfolios/{investmentRiskId}/rebalanced

Check the curl request below:
```
curl -X POST \
  http://localhost:8080/api/v1/predefined/portfolios/9/rebalanced \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '[

{
	"investmentCategoryId": 2,
	"amount": 65
},
{
	"investmentCategoryId": 3,
	"amount": 65
},
{
	"investmentCategoryId": 4,
	"amount": 567
},
{
	"investmentCategoryId": 5,
	"amount": 1265
},
{
	"investmentCategoryId": 6,
	"amount": 2465
}
```
Response:

```
[
    {
        "amount": 65,
        "investmentCategoryId": 2,
        "diffAmount": 156.35,
        "transferDetail": "Transfer $156.35 from Foreign to Bonds",
        "investmentCategoryName": "Bonds",
        "percent": 5
    },
    {
        "amount": 65,
        "investmentCategoryId": 3,
        "diffAmount": 599.05,
        "transferDetail": "Transfer $597.15 from Small Cap to Large Cap, Transfer $1.90 from Foreign to Large Cap",
        "investmentCategoryName": "Large Cap",
        "percent": 15
    },
    {
        "amount": 567,
        "investmentCategoryId": 4,
        "diffAmount": 1203.8,
        "transferDetail": "Transfer $1203.80 from Small Cap to Mid Cap",
        "investmentCategoryName": "Mid Cap",
        "percent": 40
    },
    {
        "amount": 1265,
        "investmentCategoryId": 5,
        "diffAmount": -158.25,
        "transferDetail": null,
        "investmentCategoryName": "Foreign",
        "percent": 25
    },
    {
        "amount": 2465,
        "investmentCategoryId": 6,
        "diffAmount": -1800.95,
        "transferDetail": null,
        "investmentCategoryName": "Small Cap",
        "percent": 15
    }
]

```

