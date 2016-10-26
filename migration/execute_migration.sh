docker run --rm -it -v $(pwd)/migrations:/migrations -v $(pwd):/workspace --link cassandra:cassandra test-store-migration bash -ce "(cd workspace; ruby migrate.rb /migrations/ cassandra teststore 1)"
