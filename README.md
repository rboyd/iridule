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

curl -d '{"line":"Sporer Laurine M Yellow 2032-04-09"}' -H "Content-Type: application/json" -X POST http://localhost:3000/records

curl -v http://localhost:3000/records/name |  python -m json.tool
```

## License

Copyright © 2017 Boyd
