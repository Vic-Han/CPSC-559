# Define the directories and classpath
SRC_DIR := src
BIN_DIR := bin
LIB_DIR_SQLITE := "C:\Program Files\Java\sqlite-jdbc-3.34.0.jar"
LIB_DIR_JAVAFX := "C:\Program Files\Java\javafx-sdk-21.0.2\lib"
CLASSPATH := $(BIN_DIR)

# Find all .java files in the specified directories
CLIENT_FILES := $(wildcard $(SRC_DIR)/client/*.java)
SERVER_FILES := $(wildcard $(SRC_DIR)/server/*.java)
UTILS_FILES := $(wildcard $(SRC_DIR)/Utilities/*.java)

CLIENT_CLS := $(CLIENT_FILES:$(SRC_DIR)/client/%.java=$(BIN_DIR)/client/%.class)
SERVER_CLS := $(SERVER_FILES:$(SRC_DIR)/server/%.java=$(BIN_DIR)/server/%.class)
UTILS_CLS := $(UTILS_FILES:$(SRC_DIR)/Utilities/%.java=$(BIN_DIR)/Utilities/%.class)

# Define the compiler, flags, and interpreter
JAVAC := javac
JFLAGS := -d $(BIN_DIR)/ -cp $(SRC_DIR)/
JAVA := java

# Define the targets
all: $(CLIENT_CLS) $(SERVER_CLS) $(UTILS_CLS)
	@echo "Compilation complete."

# Compile client files
$(CLIENT_CLS): $(BIN_DIR)/client/%.class: $(SRC_DIR)/client/%.java
	$(JAVAC) $(JFLAGS) --module-path $(LIB_DIR_JAVAFX) --add-modules javafx.controls $<

# Compile server files with sqlite module path
$(SERVER_CLS): $(BIN_DIR)/server/%.class: $(SRC_DIR)/server/%.java
	$(JAVAC) $(JFLAGS) --module-path $(LIB_DIR_SQLITE) $<

# Compile Utilities files
$(UTILS_CLS): $(BIN_DIR)/Utilities/%.class: $(SRC_DIR)/Utilities/%.java
	$(JAVAC) $(JFLAGS) $<

server_run: all
	@$(JAVA) -cp $(CLASSPATH) --module-path $(LIB_DIR_SQLITE) server.Master

client_run: all
	@$(JAVA) -cp $(CLASSPATH) --module-path $(LIB_DIR_JAVAFX) --add-modules javafx.controls client.ClientGUI

clean:
	rm -rf $(BIN_DIR)/*.class