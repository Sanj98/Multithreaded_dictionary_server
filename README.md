## Multithreaded_dictionary_server

This repository contains the source files of a "Multithreaded Dictionary Server".

Multiple clients can make requests to search a word, add a word and meaning(s) and delete an existing word to the server. 
The server creates a thread for each request and performs the operation by making changes to the dictionary.

- Client.java - client GUI that can make requests
- Server.java - server GUI that accepts the requests and creates a thread
- ThreadServer.java - multithreaded server that processes the request and makes changes to dictionary
- dictionary.json - dictionary file in json format

**To run the jar files**, use the following commands -

1. Client.jar
   - *java -jar Client.jar*
2. Server.jar
   - *java -jar Server.jar true/false*
   
   Mention *true* to use worker pool architecture and *false* to use thread-per-connection architecture

To understand the interaction between various classes and comparison of both the architectures, please view the pdf file. 
