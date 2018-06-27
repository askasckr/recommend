# recommend
A simple portfolio recommendation tool

The following libraries are used:
```
h2 embedded database
project lombak
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

#### Note: All the requests expect Client-Id in header to distinguish the clients.

The below seed data is populated at app startup:
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
Included: Features like ```audit fields, logging, Global Exception handler, RateLimiter, Restricting to up to 2 concurrent unique clients etc```
 
Not Included: ```Versioning, Endpoints to Investment Risk, Investment Category, AppClient (I wish I'd included them too)```

### Testcases:
--------------

Testcases are written for test endpoints, services, validators and repositories concurrent clients, rebalancing, save predefined portfolios etc.

+![Imgur](https://github.com/askasckr/recommend/blob/master/TestCoverageScreenShot.png)
+

Here is the github repo: https://github.com/askasckr/recommend
https://git.heroku.com/intense-oasis-48244.git

### Clone from it:
-----------------
```
git clone https://github.com/askasckr/recommend

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
#### Note: All the requests expect Client-Id in header to distinguish the clients and is used in ```RecsClientRateLimitInterceptor``` to control concurrent requests on two unique clients.

#### 1. To get all the predefined portfolios as list:

```
curl -X GET \
  http://localhost:8080/api/v1/predefined/portfolios \
  -H 'Cache-Control: no-cache' \
  -H 'Client-Id: test'
```

#### 2. To get all the predefined portfolios as matrix/map:

```
curl -X GET \
  http://localhost:8080/api/v1/predefined/portfolios/matrix \
  -H 'Cache-Control: no-cache' \
  -H 'Client-Id: test1'
```

#### 3. To save predefined portfolios:

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

```
curl -X POST \
  http://localhost:8080/api/v1/predefined/portfolios/9/rebalanced \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '[

{
	"investmentCategoryId": 1,
	"amount": 65
},
{
	"investmentCategoryId": 2,
	"amount": 65
},
{
	"investmentCategoryId": 3,
	"amount": 567
},
{
	"investmentCategoryId": 4,
	"amount": 1265
},
{
	"investmentCategoryId": 5,
	"amount": 2465
}
```
