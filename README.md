[DEPRECATED] substeps-runner [![Build Status](https://travis-ci.org/G2G3Digital/substeps-runner.svg)](https://travis-ci.org/G2G3Digital/substeps-runner)
===============

The substeps-api repository has been merged into one with other core Substep libraries. No more pull requests on the repository will be accepted.

So the new home is [substeps-framework](https://github.com/G2G3Digital/substeps-framework) which now contains:
 * substeps-api
 * substeps-core
 * substeps-runner
 * substeps-glossary

Runners to execute substeps, currently includes an ANT runner, a Maven plugin and a Junit runner.

1.1.3
-----
* Changes to support ExecutionListener refactoring in core and api projects
* bug with final modifier set on config class preventing it from bring set
* configurable description depth parameter

1.1.2
-----
* version number bump in line with other substeps libraries

1.1.1
-----
* A 'Catch all' in the Maven runner to handle hidden exceptions in spawned VMs 
