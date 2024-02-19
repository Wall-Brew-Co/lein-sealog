# Sealog

[![Clojars Project](https://img.shields.io/clojars/v/com.wallbrew/lein-sealog.svg)](https://clojars.org/com.wallbrew/lein-sealog)
[![GitHub](https://img.shields.io/github/license/Wall-Brew-Co/lein-sealog)](https://github.com/Wall-Brew-Co/lein-sealog/blob/master/LICENSE)
[![Twitter Follow](https://img.shields.io/twitter/follow/WallBrew?style=social)](https://twitter.com/WallBrew)

A Wall Brew [Leiningen](https://leiningen.org/) plugin for automating CHANGELOG management for projects using [Semantic Versioning 2.0](https://semver.org/ "The core specification of Semantic Versioning")

## Rationale

The [Wall Brew Open Source Standard](https://github.com/Wall-Brew-Co/open-source#software-documentation) requires libraries to publish change documentation in a human-readable format.
CHANGELOG files tend to be well-structured with embedded convenience links; however, they are almost always manually generated.
Sealog is a tool intended help automate:

* Maintaining and generating links for versions/sections
* Grouping changes by their type [(e.g. "Added", "Fixed", etc.)](https://keepachangelog.com/en/1.0.0/ "The Keep A Changelog homepage, which outlines the categories used by this application")
* Printing the [Semantic Version](https://semver.org/) number and release date

## Installation

Sealog is available as a leiningen plugin and can be downloaded from [clojars](https://clojars.org/com.wallbrew/lein-sealog).
To install Sealog, add the following in your `:plugins` list in your `project.clj` file:

```clj
[com.wallbrew/lein-sealog "1.1.0"]
```

The first time you invoke this plugin, Leiningen will automatically fetch the dependency for you.

## Usage

From the root of your project directory, you may invoke the following commands: `init`, `bump`, `render`, and `help`.

Most commands will accept several options, which can be configured by the command line arguments passed in or by a configuration file located at `.sealog/config.edn`.
In all cases, the options will follow this order of precedence:

1. Arguments passed by command line
2. Values stored in `.sealog/config.edn`
3. Default values in Sealog's implementation

### Initialize Sealog

Sealog stores [changelog entries](#change-format) in the `.sealog/changes` directory in your project.
Since new projects will not have this directory by default, Sealog may create it for you.
Additionally, Sealog will create an initial EDN change file and a configuration file for you.

```sh
$ lein sealog init
$ ls .sealog/changes -l
total 7
-rw-r--r-- 1 user user 348 Feb 19 15:36 1-0-0.edn
$ ls .sealog -l
total 8
drwxr-xr-x 2 user user 4096 Feb 19 15:37 changes
-rw-r--r-- 1 user user  128 Feb 19 15:38 config.edn
```

If the `.sealog` directory already exists, Sealog will perform no actions.

```sh
$ lein sealog init
Sealog is already initialized!
```

### Bump Version

Bump creates a new [changelog entry](#change-format) in the `.sealog/changes` directory in your project.
Based on the type of version bump, Sealog will automatically apply the correct SemVer numbering changes.
Valid bump types are: `major`, `minor`, and `patch`.

Assuming the project is currently at version `1.0.0`, `major` will create `2.0.0`

```sh
$ lein sealog bump major
Created new changelog entry: .sealog/2-0-0.edn
```

Assuming the project is currently at version `1.0.0`, `minor` will create `1.1.0`

```sh
$ lein sealog bump minor
Created new changelog entry: .sealog/1-1-0.edn
```

Assuming the project is currently at version `1.0.0`, `patch` will create `1.0.1`

```sh
$ lein sealog bump patch
Created new changelog entry: .sealog/1-0-1.edn
```

If no type is provided, Sealog will automatically apply a `patch` change.
Assuming the project is currently at version `1.0.0`:

```sh
$ lein sealog bump
Created new changelog entry: .sealog/1-0-1.edn
```

### Render Changelog

Finally, Sealog can aggregate and render your change entries into a clean, Markdown format.
By default, Sealog will render this file as `CHANGELOG.md` in the project's root.

```sh
$ lein sealog render
Wrote changelog to: CHANGELOG.md
```

Additionally, you may pass a filename for Sealog to write to instead of CHANGELOG.md

```sh
$ lein sealog render CHANGES.md
Wrote changelog to: CHANGES.md
```

If you wish to consistently default the filename to write, you may set the value of the `:changelog-filename` key in `.sealog/config.edn`

```clj
{:changelog-filename        "CHANGES.md"
 :changelog-entry-directory ".sealog/changes/"
 :version-scheme            :semver3}
```

## Change Format

Sealog stores changelog entries within a repository's `.sealog/changes` directory.
A changelog entry is an EDN file storing a map with the following keys:

* `:version` - A map containing a structured representation of a semantic version
* `:version-type` - A keyword denoting the type of versioning system used. For example, `:semver3`
* `:changed` - A map of [**Keep a Changelog**](https://keepachangelog.com/en/1.0.0/) keys, to a sequence of strings containing the changes of that type.
* `:timestamp` - A string containing an [RFC 3339](https://www.rfc-editor.org/rfc/rfc3339) timestamp.

Included is an example file for version "1.2.0" of a library.
This file would be located at `.sealog/changes/1-2-0.edn`:

```clj
{:version      {:major 1
                :minor 2
                :patch 0}
 :version-type :semver3
 :changes      {:added      ["A new function `foo` for baz-ing"
                             "Automation to deploy the project to Clojars"]
                :changed    []
                :deprecated ["`quux` was based on an old API no longer supported by source. Consumers should migrate to `foo`"]
                :removed    []
                :fixed      []
                :security   []},
 :timestamp    "2022-10-08T22:45:40.974189700Z"}
```

Empty change lists may be kept for consistency or removed.

## Configuration

Many of Sealog's commands may be modified at execution time with additional arguments.
Long-term decisions, such as where the Changelog should be written to, can also be configured in a static file.
As of version `1.1.0`, Sealog will inspect the local `.sealog/config.edn` file for configuration.

Sealog will create this file while executing `lein sealog init` if no prior Sealog configuration file is detected.
The file will be created with the defaults outlined in this README.

The following keys and values are supported:

* `:changelog-filename`
  * A string representing a filepath relative to the project root where the Markdown Changelog should be rendered.
* `:changelog-entry-directory`
  * A string representing a filepath relative to the project root where the `.edn` change files will be stored.
* `:version-scheme`
  * The versioning scheme to be assumed by Sealog.

## License

Copyright © 2022 - [Wall Brew Co](https://wallbrew.com/)

This software is provided for free, public use as outlined in the [MIT License](https://github.com/Wall-Brew-Co/lein-sealog/blob/master/LICENSE)
