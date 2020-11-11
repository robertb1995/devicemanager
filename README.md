HOW TO RUN:
1. In the root folder open Microsoft PowerShell
2. Run the following command .\runDeviceManager.bat
3. The application should now build and run, feel free to test

SWAGGER:
API documentation available at: http://localhost:8080/swagger-ui.html

DATABASE:
H2 console available at: 
http://localhost:8080/h2-console

REQUESTS FOR MANUAL TESTING (which was completed in the following sequence - all carried out using Postman - unfortunately no test collection was made):
1. POST localhost:8080/device, request body: <br> {
                                                 "deviceName" : "Name",
                                                 "brandName" : "Mayo"
                                             } <br>
                                             Adds first device with new brand name, returns OK status
                                             
2. POST localhost:8080/device, request body: <br> {
                                                 "deviceName" : "SecondName",
                                                 "brandName" : "Mayo"
                                             }  <br>
                                             Adds a second device with the same brand name, returns OK status
                                             
3. POST localhost:8080/device, request body: <br> {
                                                 "deviceName" : "KetchupName",
                                                 "brandName" : "Ketchup"
                                             }  <br>
                                             Adds a second device with the a new brand name, returns OK status                                           

4. POST localhost:8080/device, request body: <br> {
                                                 "deviceName" : "KetchupName",
                                                 "brandName" : "Ketchup"
                                             }  <br>
                                             Tries to add the same device again, gets conflict HTTP status with appropriate message
                                             
5. GET localhost:8080/device <br>
Gets all devices and returns OK status, empty list and 200 if no devices are present

6. GET localhost:8080/device/{id} <br>
Gets device under given {id} and returns OK status, if {id} is inexistent in DB returns NOT_FOUND status

7. GET localhost:8080/device?brand={brand} <br>
Gets all devices for given {brand} and returns OK status, brand is the name of the brand, empty list and 200 if no devices are present

8. DELETE localhost:8080/device/{id} <br>
Deletes the device with the given {id} and returns NO_CONTENT status, if the id is not present returns 404

9. PATCH localhost:8080/device/{id}, request body: <br>
{
    "deviceName" : "Mena",
    "brandName" : "Ketchup"
}
<br>
Partially updates the entity and returns 200, if ID inexistent returns NOT_FOUND

10. GET localhost:8080/device/{id} <br>
Checks the partially updated device with {id} to see if all was correctly updated.

11. PUT localhost:8080/device/{id} <br>
                                {
                                    "deviceName" : "vcer",
                                    "brandName" : "Aceron",
                                    "creationTime": "2025-11-10T02:43:31.953124+01:00"
                                }
                                <br>
Fully updates the given entity and returns 200, if ID is inexistent returns 404

12. GET localhost:8080/device/{id} <br>
   Checks the fully updated device with {id} to see if all was correctly updated.
   
13. DB checks after each modifying step to check if results are as expected <BR>
Expected was for Brands to not delete when device is changed or deleted (as many devices can be mapped to the same brand),
for fields to change accordingly with full and partial requests, for brand name to map correctly in device table and
when brand is deleted then devices are deleted (as expected)

14. SQL queries checked using show_sql set to true, in order to avoid n+1 problem and try to create most efficient queries.

ADDITIONAL INFORMATION:
I am aware that the Controller is lacking validations, however due to lack of time (I set aside around 6-7 hours to complete this task)
I wasn't able to implement them on time. <br>

I am aware the script is only for Windows - due to time issues no Linux was created - I would have then wrote a quick .sh

I am aware there is no security (also no time - and not in the requirements)

Some code in tests is duplicated but did not have the time to clean (same can be the case for unused imports), also tests due to lack of time are not fully completed.

I am aware an integration test with in memory would be nice to test the whole flow and also the queries (for same reason as above not implemented)

Mapper behavior is not tested - this should be tested (placeholder was left)

Dockerfile and dockercompose also not present due to lack of time,
