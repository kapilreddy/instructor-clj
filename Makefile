.PHONY: help repl nrepl test test-unit test-integration lint lint-fix clean build install compile

help:
	@echo "Available targets:"
	@echo "  repl              - Start a Clojure REPL"
	@echo "  nrepl             - Start an nREPL server on port 7888"
	@echo "  test              - Run all tests (unit + integration)"
	@echo "  test-unit         - Run unit tests only"
	@echo "  test-integration  - Run integration tests (requires OPENAI_API_KEY)"
	@echo "  lint              - Run clj-kondo linter"
	@echo "  lint-fix          - Run clj-kondo linter and copy suggestions"
	@echo "  clean             - Remove target directory"
	@echo "  build             - Build the project JAR"
	@echo "  install           - Install to local Maven repository"
	@echo "  compile           - Compile and check syntax"

repl:
	clojure -M:test -r

nrepl:
	@echo "Starting nREPL server on port 7888..."
	clojure -Sdeps '{:deps {nrepl/nrepl {:mvn/version "1.1.0"} cider/cider-nrepl {:mvn/version "0.45.0"}}}' \
		-M -m nrepl.cmdline --middleware '["cider.nrepl/cider-middleware"]' --port 7888

test:
	@echo "Running all tests..."
	clojure -M:test -m cognitect.test-runner

test-unit:
	@echo "Running unit tests only..."
	@echo "Note: Integration tests will be skipped if OPENAI_API_KEY is not set"
	clojure -M:test -m cognitect.test-runner -d test -n instructor-clj.core-test

test-integration:
	@echo "Running integration tests (requires OPENAI_API_KEY)..."
	@if [ -z "$$OPENAI_API_KEY" ]; then \
		echo "⚠️  Warning: OPENAI_API_KEY not set. Tests may be skipped."; \
		echo "Set it with: export OPENAI_API_KEY=your-key"; \
	fi
	clojure -M:test -m cognitect.test-runner -d test -n instructor-clj.integration-test

lint:
	@echo "Running clj-kondo linter..."
	clojure -M:lint --lint src test

lint-fix:
	@echo "Running clj-kondo linter with suggestions..."
	@echo "Copying fix suggestions to .clj-kondo/.cache/clj-kondo/"
	clojure -M:lint --lint src test --copy-configs

clean:
	@echo "Cleaning target directory..."
	rm -rf target .cpcache

build:
	@echo "Building JAR..."
	clojure -T:build jar

install:
	@echo "Installing to local Maven repository..."
	clojure -T:build install

compile:
	@echo "Compiling and checking syntax..."
	clojure -M -e "(require 'instructor-clj.core) (println \"✓ Code compiles successfully\")"
