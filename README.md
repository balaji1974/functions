# Spring Functions

## First Sample - newsletter-demo
```xml

1. Create a new maven project and add following dependencies to it 

   Spring web:  
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
   Function: 
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-function-web</artifactId>
    </dependency>

2. Create a model class called Subscriber inside the model package

This class will have two private fields id & email

Create getters, setters, constructor (using both fields) and toString method (using both fields)

3. Create a service class called SubscriberService inside the service package

Create the @Service annotation on the class level

Create the following: 
  List<Subscriber> subscribers=new ArrayList<>();
  AtomicInteger id=new AtomicInteger(0);

  //function to get all subscribers 
  public List<Subscriber> findAll() {
    return subscribers;
  }

  // function to create a new subscriber
  public void create(String email) {
    subscribers.add(new Subscriber(id.addAndGet(1), email));
  }
  
4. Create a class called Subscribers inside the function package

Create the @Configuration annotation on the class level

Autowire SubscriberService into this class

Create the following: 
  @Bean
  public Supplier<List<Subscriber>> findAll() {
    return () -> subscriberService.findAll();
  }
  
  @Bean
  public Consumer<String> create() {
    return (email) -> subscriberService.create(email);
  }


5. Run the server and execute the following curl commands

curl 'http://localhost:8080/create' --header 'Content-Type: application/json' --data-raw 'balaji@gmail.com'

curl 'http://localhost:8080/findAll'



6. Adding the cloud platform adapter

For AWS
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-function-adapter-aws</artifactId>
    </dependency>

For GCP
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-function-adapter-gcp</artifactId>
    </dependency>

For Azure
  <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-function-adapter-azure</artifactId>
    </dependency>

We will the deploying our package on AWS but the steps are same on all cloud platforms.


7. Adding the maven thin plugin and shade plugin 

In the plug in section comment the following:

        <!--
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
      -->

Next add the following plugins:

      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <dependencies>
          <dependency>
            <groupId>org.springframework.boot.experimental</groupId>
            <artifactId>spring-boot-thin-layout</artifactId>
            <version>1.0.31.RELEASE</version>
          </dependency>
        </dependencies>
      </plugin>
      
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <dependencies>
          <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <version>2.7.4</version>
          </dependency>
        </dependencies>
        <executions>
          <execution>
            <goals>
                 <goal>shade</goal>
            </goals>
            <configuration>
              <createDependencyReducedPom>false</createDependencyReducedPom>
              <shadedArtifactAttached>true</shadedArtifactAttached>
              <shadedClassifierName>aws</shadedClassifierName>
              <transformers>
                <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                  <resource>META-INF/spring.handlers</resource>
                </transformer>
                <transformer implementation="org.springframework.boot.maven.PropertiesMergingResourceTransformer">
                  <resource>META-INF/spring.factories</resource>
                </transformer>
                <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                  <resource>META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports</resource>
                </transformer>
                <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                  <resource>META-INF/spring/org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration.imports</resource>
                </transformer>
                <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                  <resource>META-INF/spring.schemas</resource>
                </transformer>
                <transformer implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                  <resource>META-INF/spring.components</resource>
                </transformer>
              </transformers>
            </configuration>
          </execution>
        </executions>
      </plugin>


8. Create the jar file: 

Execute the following from the IDE:
mvn cleam
mvn install 

(or)

From the command line run the following: 
mvn clean package 

This will create 2 jar files, the usual standalone .jar file and AWS jar file ending with -aws.jar 
eg. newsletter-demo-0.0.1-SNAPSHOT-aws.jar

This package can be deployed on AWS


9. Deploy the package on AWS 
Login into the AWS console 

Go to Lambda screen 

Create function -> Author from scratch -> Function name: mySubscribers -> Runtime: Java 17 -> Architecture: arm64 
-> Create Function 

Once created -> Go to code source section -> Upload from .zip or .jar file 

Next go to Run time settings -> Edit -> Handler : org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest -> Save

Now the Environment is ready for running

10. Function configration settings

Since we have multiple functions wrapped into a single jar, we have to instruct AWS how to invoke them. 
Go to Configuration tab -> Function URL -> Auth : None (this is just for testing purpose and not for production use case, where you will use IAM) 
-> Additional Settings -> Configure Cors : (tick) -> Allow Origin: * (do not do this in production) -> Save

Next copy the URL of the function 

11. Invoke the function 
Go to postman -> 

Method: POST 
URL: <url that was copied>
Header: spring.cloud.function.definition: create (Since we have mulitple functions in our jar we need to make sure which function to call)
Body: raw : balaji@gmail.com
-> Send
This will create a record using our lambda function


Method: GET 
URL: <url that was copied>
Header: spring.cloud.function.definition: findAll (Since we have mulitple functions in our jar we need to make sure which function to call)
-> Send
This will fetch all the records using our lambda function


12. Monitoring the log
Go to Cloud Watch -> Logs Group -> (Select our function) -> (click on the appropriate log stream)


That's it. We have created our first Function and executed it on Cloud using Spring Cloud Function 

```


### References:
https://www.udemy.com/course/devops-with-docker-kubernetes-and-azure-devops
