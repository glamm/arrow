# arrow-meta-prototype

**Build and run in the IDE**

```
./gradlew clean gradle-plugin:publishArrowPluginMarkerMavenPublicationToMavenLocal gradle-plugin:publishPluginMavenPublicationToMavenLocal idea-plugin:runIde -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process"
```

## Logging
Logging inside of an IntelliJ plugin is done via 
```
val LOG = com.intellij.openapi.diagnostic.Logger.getInstance("#arrow.yourLogChannel")
// LOG.error, LOG.warn, LOG.info, LOG.debug, LOG.trace are available 
```

You can see the messages in IntelliJ's log file. The log file is available at `Help->Show Log in ...`. 
debug and trace messages are not shown by default. To enable these, enter the full channel ID at 
`Help->Debug Log Settings`, for example
```
#arrow.trace
```

To enable trace message, append `:trace` to a channel ID. For example:
```
#arrow.trace:trace
```