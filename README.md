# Platform

The project will give you a presentation of skills the developer needs to have in the modern world
Quick guide:
- Make sure you have got `JDK 8` and `MongoDB` on your laptop;
- if everything set up - run the project (open bash/shell and write next commands): 
-  `mvnw package`
- `java -jar target/platform-0.0.1-SNAPSHOT.jar`
If everything was done correctly you should see no error in the console; 
#URL's is not update!! check `api layer`
First of all, letss download all stop words into our database just put next URL into your browse: `localhost:8080/platform/rest/stopwords/load`
If everything was loaded you should see `true` in the output, if so, go ahead
To start crawling go to `localhost:8080/platform/rest/crawler/start?searchword=ruby` this will start crawling vacancies that have ruby language.
It can take up to 1-2 minutes. When it is done you will get the response with crawler id: 
```JSON 
5abbdac3df6ba54ed21cd5ca
```
Check you database to get all information about crawler: 
```
{ 
    "_id" : ObjectId("5abbdac3df6ba54ed21cd5ca"), 
    "search_condition" : "ruby", 
    "status" : "PROCESSED", 
    "created_date" : ISODate("2018-03-28T18:11:15.780+0000"), 
    "modified_date" : ISODate("2018-03-28T18:11:15.780+0000"), 
    "_class" : "ua.tor.platform.persistent.Crawler"
}
```
The next step is running parser to chop description into list of seperate words:
`localhost:8080/platform/rest/parser/start?crawler_id=5abbdac3df6ba54ed21cd5ca` the var `crawler_id` is the id we got from previous step;
In this step each vacancy's description is splited into set of unique words without stop words ("i", "you", "company", "cooky", etc);
check collection `parsed_vacancy` 
```
{ 
    "_id" : ObjectId("5abbe61bdf6ba55ac49e6e5f"), 
    "crawler_id" : ObjectId("5abbdac3df6ba54ed21cd5ca"), 
    "status" : "NEW", 
    "description" : [
        "visualcraft", 
        "html5", 
        "craft", 
        "visual-craft", 
        "gem", 
        "redis", 
        "rails", 
        "visual", 
        "mvc", 
        "https", 
        "mongodb", 
        "inc", 
        "yesyk", 
        "vitalina", 
        "facebook", 
        "xbox", 
        "javascript", 
        "ruby", 
        "bundler", 
        "mercurial", 
        "rake", 
        "front-end", 
        "css3"
    ], 
    "_class" : "ua.tor.platform.persistent.ParsedVacancy"
}
 ```
This case includes stop words, but in the future the `stop_words` collection will be extended and the JSON will have 90% of skills in description
The last endpoint will create a csv dump file that will have most popular technologies: `localhost:8080/platform/rest/incrementor/start?crawler_id=5abbdac3df6ba54ed21cd5ca` 
the csv dump might start like this:
```
Skill	Quantity
ruby	47
ruby	45
rails	29
javascript 21
git	21
mysql	16
sql	15
linux	15
css	13
```
Notice the first skill and it's qauntity `ruby	47 ` is always a serach word and amount of vacancies that was processed.
All skills sorted in descending order by it's amount of vacancies, sample: skill `javascript` was appeared in 21 of 47 vacancies, the same with skill `git` and so on.



