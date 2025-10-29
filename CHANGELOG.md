# Change Log
## [1.0.0-alpha] - 2025-01-29
### Breaking Changes
- **BREAKING**: Removed automatic provider detection from core API
- The `:provider` parameter is now required for both `instruct` and `create-chat-completion` functions
- Users must explicitly specify the provider (e.g., `:openai`, `:anthropic`, `:gemini`, `:mistral`, `:ollama`)
- This change provides more control and eliminates ambiguity in provider selection

### Changed
- Updated all function signatures to require explicit `:provider` parameter
- Updated all examples and documentation to include explicit provider
- Updated all tests to pass provider explicitly

### Migration Guide
Before (0.0.1-alpha.3):
```clojure
(instruct "John Doe is 30 years old." User
          :model "gpt-3.5-turbo")
```

After (1.0.0-alpha):
```clojure
(instruct "John Doe is 30 years old." User
          :provider :openai
          :model "gpt-3.5-turbo")
```

## [0.0.1-alpha.3] - 2025-01-06
### Fixed
- FIX - Handle markdown wrapped JSON

## [0.0.1-alpha.2] - 2024-12-27
### Added
- Add openai-clojure as a dependency in project.clj
- Add support for OpenAI API compatible clients
- Add a :pre check for :api-key
- Add support for retries
- Add meeting example in README
### Changed
- Remove negations from system prompt
- Use default params throughout the namespace
- Return raw model response in case schema matching fails
- Change default max tokens to 4096
- Refactor llm->response for better debugging
- Parameterise temprature, model and max_tokens
### Fixed
- Fix typo in default client params
- Rename instructor-clj directory to instructor_clj
- FIX and improve system prompt for JSON structure
