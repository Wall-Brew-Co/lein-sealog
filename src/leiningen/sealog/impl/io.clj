(ns leiningen.sealog.impl.io
  "This namespace contains functions for reading and writing files."
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.spec.alpha :as spec]
            [clojure.string :as str]
            [leiningen.core.main :as main]
            [spec-tools.core :as st]))


(def create-file
  "Create a file with all of its parent directories."
  (comp io/make-parents io/file))


(defn list-all-files
  "List all files within a directory."
  [path]
  (let [files  (io/file path)
        dir?   (fn [f] (.isDirectory f))
        ->path (fn [f] (.getPath f))]
    (if (dir? files)
      (mapv ->path (remove dir? (file-seq files)))
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

(defn ->pretty-string
  "Return `m` as a pretty printed string"
  [m]
  (with-out-str (pp/pprint m)))

(defn write-edn-file!
  "Write the contents to a file as EDN."
  [filename content {:keys [pretty-print-edn? remove-commas-in-change-files?]}]
  (let [beautified-content (cond-> content
                             pretty-print-edn?              ->pretty-string
                             (not pretty-print-edn?)        str
                             remove-commas-in-change-files? (str/replace #"," ""))]
    (write-file! filename beautified-content)))


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
