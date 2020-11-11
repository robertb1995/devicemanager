HOW TO RUN:
1. In the root folder open Microsoft PowerShell
2. Run the following command .\runDeviceManager.bat
3. The application should now build and run, feel free to test

SWAGGER:
API documentation available at: http://localhost:8080/swagger-ui.html

DATABASE:
H2 console available at: 
http://localhost:8080/h2-console

REQUESTS FOR MANUAL TESTING (which was completed in the following sequence - all carried out using Postman - Postman collection made under DEVICE-MANAGER-TESTS.postman_collection.json in root folder):
POST localhost:8080/device, sample request body: <br> {
                                                 "deviceName" : "Name",
                                                 "brandName" : "Mayo"
                                             } <br>
                                             Adds first device with new brand name, returns OK status, Conflict if already exists
                                             
                       
GET localhost:8080/device <br>
Gets all devices and returns OK status, empty list and 200 if no devices are present

GET localhost:8080/device/{id} <br>
Gets device under given {id} and returns OK status, if {id} is inexistent in DB returns NOT_FOUND status

GET localhost:8080/device?brand={brand} <br>
Gets all devices for given {brand} and returns OK status, brand is the name of the brand, empty list and 200 if no devices are present

DELETE localhost:8080/device/{id} <br>
Deletes the device with the given {id} and returns NO_CONTENT status, if the id is not present returns 404

PATCH localhost:8080/device/{id}, sample request body: <br>
{
    "deviceName" : "Mena",
    "brandName" : "Ketchup"
}
<br>
Partially updates the entity and returns 200, if ID inexistent returns NOT_FOUND

PUT localhost:8080/device/{id} , sample request body: <br>
                                {
                                    "deviceName" : "vcer",
                                    "brandName" : "Aceron",
                                    "creationTime": "2025-11-10T02:43:31.953124+01:00"
                                }
                                <br>
Fully updates the given entity and returns 200, if ID is inexistent returns 404

ADDITIONAL INFORMATION:
I am aware that the Controller is lacking validations, however due to lack of time (I set aside around 6-7 hours to complete this whole task)
I wasn't able to implement them on time. (also not in the requirements - however I thought it still would've been nice to handle them) <br>

I am aware the script is only for Windows - due to time issues no Linux .sh was created

There is no security (not specified in the requirements)

Dockerfile and dockercompose not present due to lack of time,