This is an http server running on port 8081. It exposes the following CRUD services on localhost:8081:

/device
/hour
/day
/month

Each of the requests answers to one of the challenges. The result of a CRUD request is an output test in Json 
format. In order to generate such output, I use "javax.json". This is the only external dependency needed at runtime.

The Json strings are generated asynchronously through an ExecutorService which submits 4 tasks, one for each
output response. Such Json are maps: information to retrieve/number of impressions.

The DataObject class used to gather content of the csv file is general enough to support with minimal code change 
the treatment of columns that could be added later.

I have included a Junit test which elaborates a test csv file to extract results through the DataObject constructor
and compares them to expected ones.



