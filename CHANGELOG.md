# Change Log
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

