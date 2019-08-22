## REST API for the catalog of products



##### by [Danuka Praneeth](https://danukap.com) 


## Steps to build the jar file and run the service

clone the git repository to your local server
    
    link - https://github.com/DanukaPraneeth/product-catalog-api.git
   
Go to the local repository, change "**application.properties**" if required to change the default custom configurations and execute the below command to build the pack

``` 
mvn clean install
```
Then create the mysql database, import the tables given in 'mysql_schemas.sql' inside db-scripts folder using below command

```
mysql> source mysql_schemas.sql
```

Then start the service using below command (Java 8 is a product pre-requisit to run this service)

```
java -jar target/adcash-product-catalog-0.0.1-SNAPSHOT.jar
```




### Development Approaches

* The Rest interface of the API was implemented using a spring boot project and java MVC pattern was followed throughout the project design.
* Spring boot JPA support was not used for database related data manupulations and a seperate data base transaction layer was developed to handle all the db related functions.
* Database design was implemented in tables considering the many-to-many relationship between products and categories. You can see the db design at [here](https://drive.google.com/open?id=1hKXyZyo4eoRTI4EeMAwgK460RkzLkioq).
* Steps followed in developing this project can be viewed in the commits of the git repository.


### Implementation

#### Data Access Layer
- Mysql tables are used to store API and user related details.
- Mysql batch updates are used for database updates in a group of items and mysql transactions are used to make sure each valid API request is processed completely or rejected in an error.
- Mysql exceptions captured in this layer is throws as related custom exceptions to the business layer


#### Error Handling
- Errors are handled separately in each layer and transferred to the it's parent layers as suitable custom exceptions. 
- Finally a error specific error response is delivered to the end user


#### User Management
- Admin users can access all the APIs without any restrictions
- Non-admin users can only access the APIs to search information (search products, categories and users)
- Each API request should have the basic auth header with valid username and password
- Any user should register in the system through an admin user to access any of these APIs. Requests without a valid basic auth token will be rejected.
- Password of the users are stored in dabase after encrypting via the spring *'BCryptPasswordEncoder'*


### Further Improvements
- Unit tests to cover the full functionality of the APIs


## How to use the API service

- Create the database and tables, update the application.property file and insert the super admin details to the 'user' tables.
- Below are few sample request for the default port in local server. You may change the domain name and port depending on your requirement
- You can access detailed information on all the supported API requests at [here](https://docs.google.com/document/d/104M6wz4dVreJxKt7KNyKFW7mGhT4KOhuAQhq45owCXk/edit?usp=sharing)
- Products are uniquely identified by a **'product code'** which should be used to update and delete an existing product.


#### Get User Details

```
    curl -X GET \
      http://localhost:8080/v1/search/user \
      -H 'Authorization: Basic cm9vdDpwYXNzd29yZA==' \
      -H 'cache-control: no-cache'
```

#### Add New Users

```
    url -X POST \
      http://localhost:8080/v1/add/user \
      -H 'Authorization: Basic cm9vdDpwYXNzd29yZA==' \
      -H 'Content-Type: application/json' \
      -H 'cache-control: no-cache' \
      -d '[
        {
            "name": "user1",
            "password": "user1password",
            "admin_user": true
        },
         {
            "name": "user2",
            "password": "user2password",
            "admin_user": false
        }
    ]'
```


#### Add New Category List

```
    curl -X POST \
      http://localhost:8080/v1/add/category/ \
      -H 'Authorization: Basic cm9vdDpwYXNzd29yZA==' \
      -H 'Content-Type: application/json' \
      -H 'cache-control: no-cache' \
      -d '{
       "categories":[
          "Ladies",
          "Sales"    ]
    }'
```

#### Add New Product List

```
    curl -X POST \
      http://localhost:8080/v1/add/product \
      -H 'Authorization: Basic cm9vdDpwYXNzd29yZA==' \
      -H 'Content-Type: application/json' \
      -H 'cache-control: no-cache' \
      -d '{
       "product_list":[
          { 
             "product_name":"saree",
             "product_code":"102",
             "product_category" : ["Ladies", "Sales"]
          },
          {  
            
             "product_name":"frock",
             "product_code":"202",
             "product_category" : ["Sales"]
          }
       ]
    }'
```


#### Update Existing Product List

```
    curl -X PUT \
      http://localhost:8080/v1/update/product \
      -H 'Authorization: Basic cm9vdDpwYXNzd29yZA==' \
      -H 'Content-Type: application/json' \
      -H 'Postman-Token: f964d51e-d913-4d26-8839-88e39e07ac9d' \
      -H 'cache-control: no-cache' \
      -d '[
      {
          "product_code" : 102,
          "new_product_details":{
            "product_name": "saree",
            "product_code": 10,
            "product_category" : ["Damaged", "Gents"]
            }
      },
        {
          "product_code" : 202,
          "new_product_details":{
              "product_name": "frock",
              "product_code": 25,
                 "product_category" : ["Damaged", "Ladies", "Mega Sales", "Gents"]
            }
      }
    ]'
```
