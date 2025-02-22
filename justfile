@_list:
    just --list --unsorted

host := `uname -a`

run INPUT OUTPUT:
    ./gradlew :run --console plain --args="--input={{ INPUT }} --output={{ OUTPUT }}"
