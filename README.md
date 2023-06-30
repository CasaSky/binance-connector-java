# Binance connector and portfolio REST API

This project is providing a connector to the binance REST API built in java code.
It's using JDK 17 native image and spring boot 3.1.

Also, it provides a real-time lookup of the binance account wallets using Spring Controller.

# Getting Started

### Build and deploy the application to local server on top of a raspberry pi

Build jar file. The binance secrets are required in order to read personal account information.

To interact with binance the secrets should be provided as environment variables:

```
$ ./gradlew clean build -Dbinance-api-key=hide -Dbinance-secret-key=hide -x test
```

Copy jar file to raspberry local server:

```
$ scp build/libs/binance-connector-java-0.0.1-SNAPSHOT.jar casasky@casasky.local:/home/casasky/Apps
```

Connect to server via ssh:

```
$ ssh casasky@casasky.local
```

Run the application in the background:

```
$ nohup java -Dserver.port=9090 -Dbinance-api-key=hide -Dbinance-secret-key=hide -jar Apps/binance-connector-java-0.0.1-SNAPSHOT.jar &
```

Check application logs:

```
$ tail -f nohup.out
```

Stop the application:

```
$ kill $(pgrep java)
```

### Docker Compose support

This project contains a Docker Compose file named `compose.yaml`.

However, no services were found. As of now, the application won't start!

Please make sure to add at least one service in the `compose.yaml` file.

## GraalVM Native Support

This project has been configured to let you generate either a lightweight container or a native executable.
It is also possible to run your tests in a native image.

### Lightweight Container with Cloud Native Buildpacks

To create the image, run the following goal:

```
$ ./gradlew bootBuildImage
```

Then, you can run the app like any other container:

```
$ docker run --rm -p 8080:8080 binance-connector-java:0.0.1-SNAPSHOT
```

### Executable with Native Build Tools

Use this option if you want to explore more options such as running your tests in a native image.
The GraalVM `native-image` compiler should be installed and configured on your machine.

NOTE: GraalVM 22.3+ is required.

To create the executable, run the following goal:

```
$ ./gradlew nativeCompile
```

Then, you can run the app as follows:

```
$ build/native/nativeCompile/binance-connector-java
```

You can also run your existing tests suite in a native image.
This is an efficient way to validate the compatibility of your application.

To run your existing tests in a native image, run the following goal:

```
$ ./gradlew nativeTest
```

