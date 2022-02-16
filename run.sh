cd /Users/ajthom90/IdeaProjects/cdc-data-puller
./gradlew clean shadowJar
java -Druntime.dir=/Users/ajthom90/IdeaProjects/cdc-data-puller -jar build/libs/cdc-data-puller.jar
# shellcheck disable=SC2035
git stage *.csv
git commit -m "update data"
git push
