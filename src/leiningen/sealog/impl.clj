(ns leiningen.sealog.impl
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.spec.alpha :as spec]
            [clojure.string :as str]
            [leiningen.core.main :as main]
            [leiningen.sealog.types.changelog :as changelog]
            [leiningen.sealog.types.config :as config]
            [spec-tools.core :as st]))


(defn concatv
  "Concatenates the given sequences into a vector.
   This is a workaround for the fact that `concat` returns a lazy sequence."
  [& vectors]
  (vec (apply concat vectors)))


(defn list-all-files
  "Recursively list all files within a directory."
  [path]
  (let [files  (io/file path)
        dir?   (fn [f] (.isDirectory f))
        ->path (fn [f] (.getPath f))]
    (if (dir? files)
      (mapv ->path (filter (complement dir?) (file-seq files)))
      (throw (ex-info "Not a directory!" {:path path})))))


(defn file-exists?
  "Returns true if `path` points to a valid file"
  [path]
  (and (string? path)
       (.exists (io/file path))))


(defn write-file!
  "This is a wrapper around `spit` that logs the filename to the console."
  [filename content]
  (main/info (format "Writing to %s" filename))
  (spit filename content))

(defn write-edn-file!
  "Write the contents to a file as EDN."
  [filename content {:keys [pretty-print-edn?]}]
  (println "pretty-print-edn? " pretty-print-edn?)
  (if pretty-print-edn?
    (write-file! filename (with-out-str (pp/pprint content)))
    (write-file! filename content)))


(defn read-file!
  "This is a wrapper around `slurp` that logs the filename to the console."
  [filename]
  (main/info (format "Reading from %s" filename))
  (slurp filename))


(defn read-edn-file!
  "Reads an EDN file and returns the contents as a map.
   Throws an exception if the file does not exist, or if the contents do not coerce"
  [filename spec]
  (if (file-exists? filename)
    (let [file-content (edn/read-string (read-file! filename))
          contents     (st/coerce spec file-content st/string-transformer)]
      (if (spec/valid? spec contents)
        contents
        (throw (ex-info (str "Invalid file contents: " filename)
                        {:filename filename
                         :errors   (spec/explain-data spec contents)}))))
    (throw (ex-info "Not matching file exists!"
                    {:filename filename}))))


(defn load-config!
  "Load the configuration file."
  []
  (if (file-exists? config/config-file)
    (read-edn-file! config/config-file ::config/config)
    (do (main/info "No configuration file found. Assuming default configuration.")
        config/default-config)))


(defn load-changelog-entry-directory!
  "Load the changelog directory into a map of version to changelog entries."
  [{:keys [changelog-entry-directory] :as _config}]
  (let [files   (list-all-files changelog-entry-directory)
        reducer (fn [acc file]
                  (let [content (read-edn-file! file ::changelog/entry)]
                    (conj acc content)))]
    (reduce reducer [] files)))


(defn sealog-initialized?
  "Returns true if the sealog directory exists."
  [{:keys [changelog-entry-directory] :as config}]
  (if (file-exists? changelog-entry-directory)
    (boolean (seq (load-changelog-entry-directory! config)))
    false))


(defn render-preamble
  "Render the preamble of the changelog."
  []
  ["# Changelog"
   ""
   "All notable changes to this project will be documented in this file."
   ""
   "The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)"
   ""])


(defn render-footer
  "Render the footer of the changelog, and a notice about changelog tooling."
  []
  ["## Source"
   ""
   "This changelog was generated by [sealog.](https://github.com/Wall-Brew-Co/lein-sealog)"
   "Please do not edit it directly. Instead, edit the source data files and regenerate this file."
   ""])


(defn ->entry-title
  "Convert a changelog entry into a title for the changelog."
  [{:keys [timestamp] :as change}]
  (let [friendly-time    (first (str/split (str timestamp) #"T"))
        friendly-version (changelog/render-version change)]
    (format "%s - %s" friendly-version friendly-time)))


(defn render-toc
  "Render the table of contents for the changelog."
  [changelog]
  (let [->header (fn [entry]
                   (let [entry-title (->entry-title entry)
                         entry-link  (str/replace (str/replace entry-title #" " "-") #"\." "")]
                     (format "* [%s](#%s)" entry-title entry-link)))
        toc      (mapv ->header changelog)]
    (concatv ["## Table of Contents" ""] toc [""])))


(defn render-change-set
  "Render the set of changes for a given version."
  [{:keys [added changed deprecated removed fixed security]}]
  (let [->change-entry (fn [change] (format "  * %s" change))
        ->change       (fn [change-type changes]
                         (if (seq changes)
                           (let [heading (format "* %s" change-type)
                                 entries (mapv ->change-entry changes)]
                             (concatv [heading] entries))
                           []))]
    (concatv (->change "Added" added)
             (->change "Changed" changed)
             (->change "Deprecated" deprecated)
             (->change "Removed" removed)
             (->change "Fixed" fixed)
             (->change "Security" security))))


(defn render-changelog-entry
  "Render the changelog entry for a specific version."
  [{:keys [changes] :as change}]
  (let [header      (str "## " (->entry-title change))
        change-list (render-change-set changes)]
    (concatv [header ""] change-list [""])))


(defn render-changes
  "Render the changes for a given changelog."
  [changelog]
  (flatten (mapv render-changelog-entry changelog)))


(defn render-changelog
  "Render the changelog."
  [changelog]
  (let [preamble (render-preamble)
        toc      (render-toc changelog)
        changes  (render-changes changelog)
        footer   (render-footer)]
    (str/join "\n" (concatv preamble toc changes footer))))


(defn init!
  "Create a new changelog directory."
  [{:keys [changelog-entry-directory version-scheme] :as config}]
  (let [initial-entry          (changelog/initialize version-scheme)
        initial-entry-filename (str changelog-entry-directory (changelog/render-filename initial-entry))
        entry                  (update initial-entry :timestamp str)]
    (io/make-parents (io/file initial-entry-filename))
    (write-edn-file! initial-entry-filename entry config)))


(defn sealog-configured?
  "Returns true if the sealog configuration file exists."
  []
  (file-exists? config/config-file))


(defn configure!
  "Create a new configuration file."
  [_opts]
  (io/make-parents (io/file config/config-file))
  (write-file! config/config-file config/default-config))
