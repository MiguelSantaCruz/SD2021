make:
	javac $(shell find . -name "*.java") -d ./bin
run:
	java -cp ./bin src.mainTestUI
doc:
	javadoc $(shell find . -name "*.java") -d ./doc
