(ns leiningen.sealog.config
  "Namespace for configuring Sealog's behavior.
   Currently a place to store static/magic values, but designed to be forwards compatible with a CLI.")


(def changelog-filename
  "The default name of the Rendered Changelog file."
  "CHANGELOG.md")


(def changelog-directory
  "The default name of the directory containing the changelog entries."
  ".sealog/changes/")


(def default-scheme
  "The default versioning scheme to use."
  :semver3)
