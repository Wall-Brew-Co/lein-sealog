(ns leiningen.sealog
  "Main namespace for the sealog plugin.

   Provides the entry point for leiningen and basic help functions."
  (:require [leiningen.core.main :as main]
            [leiningen.sealog.api :as sealog]))


(defn unknown-command
  "Formats an error message for an unknown command."
  [command]
  (str "Unknown command: " command "\nAvailable commands: init, bump, render, and help"))


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


(defn help
  "Display help text for a specific command."
  [options]
  (let [command (first options)]
    (case command
      nil        (top-level-help)
      "init"     (init-help)
      "bump"     (bump-help)
      "render"   (render-help)
      "help"     (main/info "Run `lein sealog help <command>` for more information on a specific command.")
      (main/info (unknown-command command)))))


#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}


(defn sealog
  "Manage youur changelog, programatically."
  [_project & args]
  (let [command (first args)
        options (rest args)]
    (case command
      "init"   (sealog/init options)
      "bump"   (sealog/bump-version options)
      "render" (sealog/render-changelog options)
      "help"   (help options)
      (main/warn "Unknown command: %s" command))))
