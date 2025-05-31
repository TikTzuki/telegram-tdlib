# Telegram TDLib Java Wrapper

A Java library for interacting with the [Telegram TDLib](https://docs.tiktuzki.com/telegram-tdlib) (Telegram Database Library). This wrapper provides a simple and idiomatic Java interface to the native TDLib, enabling you to build Telegram clients, bots, and automation tools in Java.

## Features
- Full access to Telegram's core features via TDLib
- Simple, object-oriented Java API
- Cross-platform: works on macOS, Linux, and Windows
- Includes prebuilt native binaries for easy setup

## Getting Started

### 1. Add Dependency
Add the following to your `build.gradle`:

```
maven { url 'https://reposilite.tiktuzki.com/releases' }
```

And in your dependencies block:

```
implementation 'org.tiktuzki:telegram-tdlib:1.0.0'
```

### 2. Native Library
The required native library (`libtdjni`) is included in the resources. On macOS, it's `libtdjni.dylib`.

If you need to use a different platform, ensure the correct native library is available in your classpath or system library path.

### 3. Example Usage

```java
import org.tiktuzki.telegram.TdApi;
import org.tiktuzki.telegram.TdClient;

public class Example {
    public static void main(String[] args) {
        Client.setLogMessageHandler(0, new LogMessageHandler());

        // disable TDLib log and redirect fatal errors and plain log messages to a file
        try {
            Client.execute(new TdApi.SetLogVerbosityLevel(0));
            Client.execute(new TdApi.SetLogStream(new TdApi.LogStreamFile("tdlib.log", 1 << 27, false)));
        } catch (Client.ExecutionException error) {
            throw new IOError(new IOException("Write access to the current directory is required"));
        }

        // create client
        client = Client.create(new UpdateHandler(), null, null);
    }
}
```

## Documentation
- [TDLib Official Docs](https://docs.tiktuzki.com/telegram-tdlib)
- Javadocs are available in `src/main/resources/docs/` or online (if published).

## License
This project is licensed under the terms described in `src/main/resources/docs/legal/`.

## Contributing
Pull requests and issues are welcome! Please open an issue to discuss your ideas or report bugs.

## Acknowledgements
- [Telegram TDLib](https://docs.tiktuzki.com/telegram-tdlib)
- Inspired by the open-source Telegram community

# Test is jni work
javac -cp build/libs/telegram-tdlib.jar Test.java \
java -cp build/libs/telegram-tdlib.jar:. Test