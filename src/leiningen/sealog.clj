(ns leiningen.sealog
  ;; The ns docstring prints as the task description in `lein help`
  "Update your changelog, programmatically."
  (:require [leiningen.core.main :as main]
            [leiningen.sealog.api :as sealog]))


(defn unknown-command
  "Formats an error message for an unknown command."
  [command]
  (str "Unknown command: " command "\nAvailable commands: init, bump, render, insert, version, check, and help"))


(defn top-level-help
  "Display help text for sealog in general"
  []
  (main/info "Sealog - A changelog generator for Clojure projects.")
  (main/info "")
  (main/info "Usage: lein sealog <command> [options]")
  (main/info "")
  (main/info "Available commands:")
  (main/info "  init    - Initialize a new changelog directory and a default configuration file.")
  (main/info "  bump    - Bump the version number and create a new changelog entry.")
  (main/info "  render  - Render the changelog to the target file.")
  (main/info "  insert  - Insert a note of a specified change type into the most current change file.")
  (main/info "  version - Display information about the current version.")
  (main/info "  check   - Check the current configuration, changelog entries, and the current project version.")
  (main/info "  help    - Display this help message.")
  (main/info "")
  (main/info "Run `lein sealog help <command>` for more information on a specific command."))


(defn init-help
  "Display help text for the init command"
  []
  (main/info "Usage: lein sealog init")
  (main/info "")
  (main/info "Initialize a new changelog directory and configuration file.")
  (main/info "If a changelog directory already exists, this command will do nothing."))


(defn bump-help
  "Display help text for the bump command"
  []
  (main/info "Usage: lein sealog bump <bump-type>")
  (main/info "")
  (main/info "Create a new changelog entry for the version bump.")
  (main/info "")
  (main/info "Available bump types:")
  (main/info "  major - Bump the major version number.")
  (main/info "  minor - Bump the minor version number.")
  (main/info "  patch - Bump the patch version number."))


(defn render-help
  "Display help text for the render command"
  []
  (main/info "Usage: lein sealog render <file-path>")
  (main/info "")
  (main/info "Render the changelog to the target file.")
  (main/info "")
  (main/info "Options:")
  (main/info "  <file-path>  - File where the Changelog will be rendered.")
  (main/info "                 If no value is provided, the changelog-filename value in sealog/config.edn will be used.")
  (main/info "                 If no configuration file is found, CHANGELOG.md will be set as the default."))


(defn insert-help
  "Display help text for the insert command"
  []
  (main/info "Usage: lein sealog insert <change-type> \"<change-message>\" \"<change-message>\" ...")
  (main/info "")
  (main/info "Insert a note of a specified `<change type> into the most current change file.")
  (main/info "Requires at least one `<change-message>` to be provided.")
  (main/info "However, multiple `<change-message>` can be provided.")
  (main/info "")
  (main/info "Options:")
  (main/info "  <change-type> - A valid change type: (\"added\", \"changed\", \"deprecated\", \"removed\", \"fixed\", \"security\")")
  (main/info "                  If no value is provided, process will exit abnormally.")
  (main/info "  <change-message> - One or more double-quoted strings to be added to the change file.")
  (main/info "                     If no value is provided, process will exit abnormally.")
  (main/info "                     All values must be unique."))


(defn version-help
  "Display help text for the version command"
  []
  (main/info "Usage: lein sealog version <source>")
  (main/info "")
  (main/info "Display information about the current version.")
  (main/info "")
  (main/info "Options:")
  (main/info "  <source> - A valid source of project version information: (\"project.clj\", \"sealog\")")
  (main/info "             If no value is provided, the process will load all sources to compare."))


(defn check-help
  "Display help text for the check command"
  []
  (main/info "Usage: lein sealog check")
  (main/info "")
  (main/info "Check the current configuration, changelog entries, and the current project version.")
  (main/info "Will exit abnormally if any issues are found.")
  (main/info "")
  (main/info "Checks:")
  (main/info "  - If a configuration file exists, it must be valid.")
  (main/info "  - The changelog directory must contain at least one file.")
  (main/info "  - The changelog directory must contain only valid changelog entries.")
  (main/info "  - All changelog entry files must use the same version type.")
  (main/info "  - All changelog entry files must have a distinct version.")
  (main/info "  - The project version must match the latest changelog entry.")
  (main/info "  - The rendered changelog must contain all changelog entries."))


(defn help
  "Display help text for a specific command."
  [options]
  (let [command (first options)]
    (case command
      nil        (top-level-help)
      "init"     (init-help)
      "bump"     (bump-help)
      "render"   (render-help)
      "insert"   (insert-help)
      "version"  (version-help)
      "check"    (check-help)
      "help"     (main/info "Run `lein sealog help <command>` for more information on a specific command.")
      (main/info (unknown-command command)))))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}


(defn sealog
  "Manage your changelog, programmatically."
  [project & args]
  (let [command (first args)
        options (rest args)]
    (case command
      "init"    (sealog/init project options)
      "bump"    (sealog/bump-version project options)
      "render"  (sealog/render-changelog project options)
      "insert"  (sealog/insert-entry project options)
      "version" (sealog/display-version project options)
      "check"   (sealog/check project options)
      "help"    (help options)
      (main/warn "Unknown command: %s" command))))
