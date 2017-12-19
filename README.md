# iridule

Parse and index some dummy data, render json over web endpoints.

## Usage

Build:
```
lein uberjar
```

Create input files with:
```
(spit "/tmp/test1.csv" (clojure.string/join "\n"(repeatedly 500000 #(iridule.data-test/gen-row " "))))
```

Invoke from cmdline:

```
java -jar target/iridule-0.1.0-SNAPSHOT-standalone.jar /tmp/test1.csv /tmp/test2.csv /tmp/test3.csv
```

Documentation is available at http://localhost:3000/ while running

Index further records, and verify operation.
```
# load all names in a file over http
cat /tmp/test4.csv | sed "s/\'/\\\\'/g" | xargs -I {} curl -d '{"line":"{}"}' -H "Content-Type: application/json" -X POST http://localhost:3000/records

# query an index, format json
curl -v http://localhost:3000/records/name |  python -m json.tool
```



## License

Copyright © 2017 Robert Boyd
