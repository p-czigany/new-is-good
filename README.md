# new-is-good

Demonstrates that the 'new' keyword in Java does not necessarily mean tight
coupling.

In your usual Spring Boot or Jakarta EE application, the "new" keyword is a red
flag as it means an object creating its own dependencies. That indeed results in
tight coupling and the lack of testability.

The above is associated with the use of the "new" keyword while to me it also
speaks volumes about today's popular frameworks, like Spring Boot or Jakarta EE.

## Start the Application

Use Java 21+

./mvnw clean package

java -cp "target/new-is-good-1.0-SNAPSHOT.jar:target/lib/*" dev.pczigany.newisgood.App
