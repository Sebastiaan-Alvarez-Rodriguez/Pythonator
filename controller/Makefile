TARGET := pythonator-pi
BUILD := build
INCLUDE := include

COMMON_FLAGS := -Wall \
	-Wextra \
	-march=native \
	-O3 \
	$(shell pkg-config --cflags opencv4) \
	-I$(INCLUDE)

CXXFLAGS += \
	-std=c++17 \
	$(COMMON_FLAGS)
	
CFLAGS += \
	-std=c11 \
	$(COMMON_FLAGS)
	
LIBS := -lpthread -lbluetooth $(shell pkg-config --libs opencv4)
LDFLAGS += -flto $(LIBS)

CPPSRC := $(shell find src/ -type f -name "*.cpp" -print)
CSRC := $(shell find src/ -type f -name "*.c" -print)
OBJ := $(CPPSRC:%=$(BUILD)/%.o) $(CSRC:%=$(BUILD)/%.o)

all: $(TARGET)

$(TARGET): $(OBJ)
	@echo Linking $(subst $(BUILD)/,,$@)
	@mkdir -p $(dir $@)
	@$(CXX) -o $@ $^ $(LDFLAGS)

$(BUILD)/%.cpp.o: %.cpp
	@echo Compiling $(subst $(BUILD)/,,$<)
	@mkdir -p $(dir $@)
	$(CXX) -MMD $(CXXFLAGS) -c -o $@ $<

$(BUILD)/%.c.o: %.c
	@echo Compiling $(subst $(BUILD)/,,$<)
	@mkdir -p $(dir $@)
	@$(CC) -MMD $(CFLAGS) -c -o $@ $<

clean:
	@rm -rf $(BUILD) $(TARGET)

-include $(shell find $(BUILD)/ -type f -name "*.d" -print 2>/dev/null)

.PHONY: clean run
