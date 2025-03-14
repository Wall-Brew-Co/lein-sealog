# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)

## Table of Contents

* [1.9.0 - 2025-01-20](#190---2025-01-20)
* [1.8.0 - 2024-10-13](#180---2024-10-13)
* [1.7.0 - 2024-09-21](#170---2024-09-21)
* [1.6.0 - 2024-05-03](#160---2024-05-03)
* [1.5.0 - 2024-05-03](#150---2024-05-03)
* [1.4.0 - 2024-05-01](#140---2024-05-01)
* [1.3.0 - 2024-03-13](#130---2024-03-13)
* [1.2.1 - 2024-03-10](#121---2024-03-10)
* [1.2.0 - 2024-03-03](#120---2024-03-03)
* [1.1.0 - 2024-02-19](#110---2024-02-19)
* [1.0.2 - 2022-12-11](#102---2022-12-11)
* [1.0.1 - 2022-10-23](#101---2022-10-23)
* [1.0.0 - 2022-10-23](#100---2022-10-23)

## 1.9.0 - 2025-01-20

* Added
  * Added a new configuration key `:remove-commas-in-change-files?` to remove automatically printed commas in change entry files.

## 1.8.0 - 2024-10-13

* Added
  * Support for loading configuration from `project.clj`.
  * Support for loading configuration from `.wallbrew/sealog/config.edn`.

## 1.7.0 - 2024-09-21

* Fixed
  * Added the `<organization>` tag to the pom.xml file.

## 1.6.0 - 2024-05-03

* Changed
  * Removed dependency on `org.clojure/core.match`.
  * Removed dependency on `lein-project-version`.
  * Downgraded `org.clojure/test.check` to `:dev` profile for tests.

## 1.5.0 - 2024-05-03

* Added
  * `check` command to [verify the current configuration, changelog entries, and project version.](https://github.com/Wall-Brew-Co/lein-sealog#check-sealog-configuration)

## 1.4.0 - 2024-05-01

* Added
  * `version` command to grant CLI access to [the current version.](https://github.com/Wall-Brew-Co/lein-sealog#view-version-information)

## 1.3.0 - 2024-03-13

* Added
  * Create `insert` command to grant CLI access to [creating new changelog entries.](https://github.com/Wall-Brew-Co/lein-sealog#inserting-new-change-entries)

## 1.2.1 - 2024-03-10

* Security
  * Bumped `org.clojure/clojure` to `1.11.2`. Note: Addressing `CVE-2024-22871` / `GHSA-vr64-r9qj-h27f` requires consumers to upgrade to this version.

## 1.2.0 - 2024-03-03

* Added
  * Added `:pretty-print-edn?` to the configuration. EDN files for changelog entries and configuration may now be pretty printed.

## 1.1.0 - 2024-02-19

* Added
  * Created a [configuration file](https://github.com/Wall-Brew-Co/lein-sealog?tab=readme-ov-file#configuration) for sealog consumers.
* Changed
  * Sealog commands will now use the configuration file if it exists and no command line options are set.

## 1.0.2 - 2022-12-11

* Fixed
  * Updated footer link to point to the public repository.

## 1.0.1 - 2022-10-23

* Fixed
  * Corrected SCM information in leiningen project file.

## 1.0.0 - 2022-10-23

* Added
  * Idempotent changelog [initialization](https://github.com/Wall-Brew-Co/lein-sealog?tab=readme-ov-file#initialize-sealog) via: `lein sealog init`
  * [Rendering changelogs to markdown files](https://github.com/Wall-Brew-Co/lein-sealog?tab=readme-ov-file#render-changelog) via: `lein sealog render`
  * [Changelog version management](https://github.com/Wall-Brew-Co/lein-sealog?tab=readme-ov-file#bump-version) via: `lein sealog bump`

## Source

This changelog was generated by [sealog.](https://github.com/Wall-Brew-Co/lein-sealog)
Please do not edit it directly. Instead, edit the source data files and regenerate this file.
