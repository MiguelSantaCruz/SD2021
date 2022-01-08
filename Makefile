make:
	javac $(shell find . -name "*.java") -d ./bin
run:
	java -cp ./bin mainTestUI
doc:
	javadoc $(shell find . -name "*.java") -d ./doc
