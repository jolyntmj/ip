## AI-assisted changes (ChatGPT)

### A-Assertions
- Updated parsing and list index validation so user-facing errors are handled via SealriouslyException/guards rather than AssertionError.
- Kept assertions as internal invariants after validation to satisfy “assertions usage” intent without breaking tests.

### A-Streams
- Refactored tag parsing / task listing logic using Java Streams where appropriate (kept conversions small and readable).

### A-CodeQuality / Refactoring
- Fixed task index validation boundary bug (prevented IndexOutOfBoundsException and ensured consistent SealriouslyException behavior).
- Removed unused/unsafe imports and reduced brittle dependencies.

### Javadoc
- Added missing JavaDoc to all public classes and methods.
- Added concise documentation to non-trivial private helper methods (Parser, Storage, TaskSerializer).
- Improved class-level documentation for Storage and TaskSerializer to include tag format.


### Notes / Observations
- AI was most helpful for systematically spotting missing JavaDoc and generating consistent method documentation quickly.
- Manual review still needed to ensure messages match project requirements and test expectations.
