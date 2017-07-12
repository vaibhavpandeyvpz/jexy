# Jexy
Jexy is a [Java](https://www.java.com/en/) library to make executing external process (and optionally read
**error**/**output**) streams easily a breeze with a very easy to use API.

[![Build status](https://img.shields.io/travis/vaibhavpandeyvpz/jexy.svg?style=flat-square)](https://travis-ci.org/vaibhavpandeyvpz/jexy)
[![Code Coverage](https://img.shields.io/codecov/c/github/vaibhavpandeyvpz/jexy.svg?style=flat-square)](https://codecov.io/gh/vaibhavpandeyvpz/jexy)
[![Latest Version](https://img.shields.io/github/release/vaibhavpandeyvpz/jexy.svg?style=flat-square)](https://github.com/vaibhavpandeyvpz/jexy/releases)
[![License](https://img.shields.io/badge/license-MIT-brightgreen.svg?style=flat-square)](LICENSE)

# Getting started
Jexy can be pulled from maven easily by adding it to [Gradle](https://gradle.org/) dependencies as follows:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.vaibhavpandeyvpz:jexy:1.0-SNAPSHOT'
}
```

# Usage
```java
import com.vaibhavpandey.jexy.ShellProcess;

class EntryPoint {
    
    static int main(String[] args) {
        // To run a command and just get the **exit code**
        ShellProcess process = new ShellProcess("sh", "-c", "echo 'something' > somefile.txt");
        if (0 == process.execute()) {
            System.out.println("File save successfully.");
        }
        // To run a command and retrieve the output
        ShellProcess process = new ShellProcess("sh", "-c", "cat somefile.txt");
        ShellResult result = process.execute(true, true);
        if (0 == result.getExitCode()) {
            // General output from process
            List<String> STDOUT = result.getStdOut();
            // Error output from process
            List<String> STDERR = result.getStdErr();
        }
        // To run multiple commands in one process
        ShellProcess process = new ShellProcess("sh");
        // COMMANDS >>>
        process.add("chmod", "644", "somefile.txt");
        process.add("chown", "-R", "user:group", "/var/www");
        ShellResult result = process.execute(true, true);
        // <<< COMMANDS
        if (0 == result.getExitCode()) {
            // It all ran interactively in one process
        }
    }
}
```

License
-------
See [LICENSE](LICENSE) file.
