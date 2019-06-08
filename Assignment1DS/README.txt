#############################################
# By Dean Pakavan: 757389
# Multi-threaded Dictionary Server/Client
# Assignment 1: Distirbuted Systems, semester 2, 2018
#############################################

In the src folder contains:
-Package Client with DictionaryClient.java
-Package Server with DictionaryServer.java
		     MultiServer.java
		     GUIServer.java
-Jar file for JSON package.
The lib folder also contains the jar file for the JSON package.

#############################################
To run DictionaryClient.java:
<Server-Address> <Port-Address>

E.g. localhost 1254

#############################################
To run DictionayrServer.java
<Port> <Dictionary-File>

E.g. 1254 dict.json

#############################################
Where dictionary file is the file path to the dict.json file.

You can export the packages as a RUNNABLE jar file. To run them, simply;

java -jar DictionaryClient.jar <Server-Address> <Port-Address>
java -jar DictionaryServer.jar <Port> <Dictionary-File>

