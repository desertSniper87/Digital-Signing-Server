# Sample  eSign document signing application using Spring Boot 

A simple eSign document signing application using Spring Boot with the following options:

## How to run
#Web Based Demo
`java -jar esign-cloud-server-1.0.1-SNAPSHOT.jar --esign.upload.path=[UPLOAD_DIR] --esign.base.url=[ESIGN_API_URL] --esign.auth.client=[CLIENT_ID] --esign.auth.password=[CLIENT_PASSWORD]`
#API Service
`java -jar esign-cloud-server-1.0.1-SNAPSHOT.jar --esign.upload.path=[UPLOAD_DIR]`
<br/>
Where
- esign.upload.path - the path where doc will be stored
- esign.auth.url - esign signing url
- esign.auth.client - esign clientID
- esign.auth.password - esign client Password

Open a web browser to http://localhost