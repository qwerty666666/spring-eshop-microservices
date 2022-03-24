Utils for Spring Data JPA.

### Natural ID support for repositories

Add support for Hibernate's ```Session.bySimpleNaturalId()``` in Repositories.

```java
var cart = cartRepository.findByNaturalId("cart-natural-id");
```

To add ```findByNaturalId``` support you should overwrite repositoryBaseClass:
```java
@Configuration
@EnableJpaRepositories(repositoryBaseClass = SimpleNaturalIdRepositoryImpl.class)
class Config {
}
```

```java
interface CartRepository extends SimpleNaturalIdRepository<Cart, Long, String> {
}
```