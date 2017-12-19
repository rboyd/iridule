# iridule

Parse and index some dummy data, render json over web endpoints.

## Usage

Create input files with:
```
(spit "/tmp/test1.csv" (clojure.string/join "\n"(repeatedly 500000 #(iridule.data-test/gen-row " "))))
```

Invoke from cmdline:

```
java -jar target/iridule-0.1.0-SNAPSHOT-standalone.jar /tmp/test1.csv /tmp/test2.csv /tmp/test3.csv
```

## License

Copyright © 2017 Boyd
