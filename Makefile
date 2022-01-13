make:
	javac $(shell find . -name "*.java") -d ./bin
run:
	java -cp ./bin Client
doc:
	javadoc $(shell find . -name "*.java") -d ./doc
