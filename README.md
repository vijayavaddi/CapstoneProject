# DoctorPatientService

**UseCase**: To Create a Doctor Patient REST Service and to manage all the basic CRUD operations.
_________________________________________________________________________________________________________________________________________________________
**Tech Stacks Used:** Framework: Spring Boot Build Tool: Maven Database: PostgreSQL API Documentation: Swagger UI (OpenAPI) Testing & Quality: JUnit, Mockito, JaCoCo, SonarQube Logging: SLF4J with Logback
_________________________________________________________________________________________________________________________________________________________

**Expected Output is:** Should be able to run the service with the source code or via the jar. Should be able to make GET, POST, PUT and DELETE requests. Should be able to check the data in the database. Custom exceptions should be thrown if it makes sense. Input validation should be done wherever it makes sense. Should return ResponeEntity with appropriate status codes.
_________________________________________________________________________________________________________________________________________________________

Entites which provided in usecase was Doctor and Patient, But we create some more entites to acheive the service

Doctor - doctor_id, specialization_id, experience, qualification
Patient - patient_id, age, blodd_group, gender
Appointment - appointment_id, doctor_id, patient_id, from_time, disease_name, status, to_time
Users - id, first_name, last_name, mobile, password, role, created_at, updated_at
Specialization - specialization_id, specialization_name
Diseases - disease_id, specialization_id, disease_name

**Usecase Requirement:**
* A doctor can have maximum 4 patients and minimum zero patients.
* Adding a 5th patient throws a custom exception.
* A patient can:
* A patient can be created and can exists without attached to
  any doctor.
* A patient can be later attached to any doctor.
* A patient can be attached to only one doctor.
* A patient can be removed from one doctor and can be attached
  to another doctor (if the other doctor have less then 4 patients
_________________________________________________________________________________________________________________________________________________________
**API Documentation:**
_________________________________________________________________________________________________________________________________________________________

**Swagger UI available at:** http://localhost:8080/swagger-ui.html
_________________________________________________________________________________________________________________________________________________________

**Each endpoint we have documented with:**  Request/Response models  HTTP methods  Status codes
_________________________________________________________________________________________________________________________________________________________

**Testing & Quality:**
_________________________________________________________________________________________________________________________________________________________

**Unit Testing:** JUnit & Mockito Code Coverage: JaCoCo Code Quality: SonarQube Logging: SLF4J (Simple Logging Facade for Java) for traceability and debugging
 _________________________________________________________________________________________________________________________________________________________

**DB Design:**
![img](https://github.com/user-attachments/assets/7de3774c-ec0a-47fb-91b8-ef76157e4012)

_________________________________________________________________________________________________________________________________________________________

**Associations Mapped:**
USERS ↔ Doctors One to One
USERS ↔ Patients One to One
Appointments ↔ Doctors One to Many
Appointments ↔ Patients One to One
Patients ↔ Diseases Many to One
Disease ↔ Specialization Many to One
Specialization ↔ Doctors One to Many
_________________________________________________________________________________________________________________________________________________________

 **Code Flow Diagram**
 ![img_1](https://github.com/user-attachments/assets/581c3237-440f-46f3-b451-0af63d8df34c)


_________________________________________________________________________________________________________________________________________________________



**Exception handling are:**
* CustomException
* DiseaseNotFoundException
* DoctorNotAvailableException
* DoctorPatientLimitExceededException
* DuplicateAppointmentException
* EntityNotFoundException
* ErrorResponse
* GlobalExceptionHandler
* InvalidOperationException
* PatientExceptionHandler
* ResourceNotFoundException
API Documentation
_________________________________________________________________________________________________________________________________________________________

**Swagger UI available at:** http://localhost:8080/swagger-ui.html
Each endpoint is documented with:
* Request/Response models
* HTTP methods
* Status codes
* Example payloads
_________________________________________________________________________________________________________________________________________________________

**Testing & Quality**
Unit Testing: JUnit & Mockito
Code Coverage: JaCoCo
Code Quality: SonarQube
Logging: SLF4J (Simple Logging Facade for Java) for traceability
and debuggin

_______________________________________________________________________________________
**DEVELOPERS**
* Vaddi Vijaya lakshmi
* Ujwal G
* Tamizhkavi K
* Tharun S
* Sri sruthi Kolluri
* Shruti Srivasthava
* Subitsha Ramalingam



//dummy data
{
  "mobile": "0000000000",
  "password": "jl",
  "roles": "string"
}
{
  "mobile": "9657834212",
  "password": "Pass@123",
  "roles": "string"
}
--------------------------
{
  "firstName": "vijaya",
  "lastName": "v",
  "mobile": "8761466955",
  "password": "Pass@123",
  "role": "PATIENT"
}
---------------------
{
  "mobile": "9524474282",
  "password": "Pass@123",
  "roles": "string"
}
{
  "mobile": "9448338979",
  "password": "Pass@123",
  "roles": "string"
}
{
  "mobile": "9675423423",
  "password": "Pass@123",
  "roles": "string"
}
{
  "mobile": "9368234762",
  "password": "Pass@123",
  "roles": "string"
}
---------------------------------
{
  "diseasename": "Gout",
  "mobile": "9675423423",
  "description": "Follow-Up",
  "fromTime": "2025-06-23T04:30:54.324Z",
  "toTime": "2025-06-23T04:30:54.324Z"
}
---------------------------------
{
  "diseasename": "Gout",
  "mobile": "9989345241",
  "description": "Follow-Up",
  "fromTime": "2025-06-23T04:30:54.324Z",
  "toTime": "2025-06-23T04:30:54.324Z"
}

select patient_id from appontments where doctor_id="" and status="SCHEDULED"


L
