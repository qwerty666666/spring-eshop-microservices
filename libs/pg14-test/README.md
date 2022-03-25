* Provides JUnit Extension that runs Postgres14 Testcontainer and set up 
Spring's ```spring.datasource.*``` properties. You just need to annotate
test class with ```@DbTest```. The database will be 
* Add [DbRider](https://github.com/database-rider/database-rider) support.

```java
@DbTest
class SomeTest {
    @Test
    @DataSet(value = "...")
    void test() {
        // ...
    }
}
```