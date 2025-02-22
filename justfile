@_list:
	just --list --unsorted

host := `uname -a`

run PATH:
    ./gradlew :run --console plain --args="--path={{PATH}}"

