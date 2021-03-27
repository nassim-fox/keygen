# Keygen

This is a simple key generator web and mobile application to generate random keys, save the keys and delete them and mark keys as used or not just as a reminder. In the mobile application you can also download the state of the keys list at a certain moment ( V3 ). 


## About
The web application was made using Django as a backend for the rest api. As for the frontend Ajax was used to make the requests and to have a responsive application.
The mobile application is an Android app coded in java. It's mainly just managing the listview by using asynctasks to make requests and call the REST APIs. 


## Usage
### web application
To use the web application you can access it from :

https://keygenapp.herokuapp.com/keygen/

the APIs links are 
https://keygenapp.herokuapp.com/keygen/api/generate ( to generate keys ) 
https://keygenapp.herokuapp.com/keygen/api/get_all ( to get all keys ) 
https://keygenapp.herokuapp.com/keygen/api/save ( to save a key ) 
https://keygenapp.herokuapp.com/keygen/api/delete ( to delete a key ) 
https://keygenapp.herokuapp.com/keygen/update ( to update a key ) 

### mobile application 
Download the apk from https://drive.google.com/file/d/1MHoyNNoQH-m4sNU5cOwHNbDybIez969w/view?usp=sharing and install it ( supports android 5 and later )

