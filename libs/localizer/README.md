Helper lib simplifying Spring's localization. It can be useful if you use localization out
of UI layer, where you don't know user Locale and have to use ```LocaleContextHolder```.

### Autoconfiguration

The lib provides auto-configured ```Localizer```, which wraps ```MessageSource``` and use
```LocaleContextHolder``` to retrieve current Locale.

> Note: to be autoconfigured Localizer needs MessageSource Bean to be registered

```java
public class Service {
    @Autowired
    private Localizer localizer;

    // my.message.code is property from message.properties or other 
    // source available from MessageSource
    
    public String getMessage() {
        // my.message.code=example message
        return localizer.getMessage("my.message.code");
    }
    
    public String getMessageWithParameters() {
        // my.message.code={0}+{1}={2}
        return localizer.getMessage("my.message.code", 1, 1, 2);
    }

    public String getMessageWithCustomLocale() {
        // my.message.code=example message
        return localizer.getMessage("my.message.code", Locale.ENGLISH);
    }
}
```