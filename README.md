
# Klasha Backend Challenge Application

![example workflow](https://github.com/engrceey/Test/actions/workflows/maven.yml/badge.svg)

## Task Description
Based on the public free API you can find here you need to build a Spring boot app with the following endpoints:
1. A GET endpoint that takes N as a parameter (number of cities) and returns the most populated N cities from Italy, New Zealand and Ghana ordered by population descending (from most populated to the least one). If in the 3 countries, there are not enough cities to cover N, please return the one you can get from the APIs.

2. An endpoint that takes a country as a parameter (e.g. Italy, Nigeria, …) and returns:
population
capital city
location
currency
ISO2&3
3. An endpoint that takes a country as a parameter (e.g. Italy, Nigeria, …) and returns the full list of all the states in the country and all the cities in each state.
4. An endpoint that takes a country as a parameter (e.g. Italy, Nigeria, …), a monetary amount and a target currency and provides:
the country currency
and using the [CSV file](exchange_rate.csv) provided converts the amount to the target currency and formats it correctly.


### Application Functionalities
* Gets most Populated Cities by N in descending order
* Gets Country Information
* Queries States and Cities
* Makes Currency Conversions Seamless  

### How to Run Locally
Clone application: git clone git@github.com:codecharlan/code-challenge.git

From any suitable IDE (IntelliJ Recommended) and with Java installed (Java 8 and Above) run application


**Postman Documentation** available at :: https://documenter.getpostman.com/view/29888943/2s9YCBu9jt

**App Profile monitor available at :: http://localhost:8080/api/v1/actuator


### Technology Used:
* Java
* SpringBoot
* Docker
* CI/CD
* Junit & Mockito
* Git and GitHub
* Postman