Utils that allow to mock SecurityContext with ```CustomJwtAuthentication``` in tests.

```java
@SpringBootTest
class Test {
    @Test
    @WithMockCustomJwtToken(customerId = "my-customer-id")
    void test() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        assertThat(auth).isInstanceOf(CustomJwtAuthentication.class);
        assertThat(((CustomJwtAuthentication) auth).getCustomerId()).isEqualTo("my-customer-id");
    }
}
```