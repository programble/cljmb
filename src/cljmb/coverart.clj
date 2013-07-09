(ns cljmb.coverart
  (:require [clj-http.client :as http]))

(def ^:dynamic *api* "http://coverartarchive.org/")

(defn- json-req [url]
  (let [resp (http/get (str *api* url)
               {:throw-exceptions false
                :as :json
                :accept :json})]
    (when (= (:status resp) 200)
      (:body resp))))

(defn release
  "Get cover art metadata for a release MBID."
  [mbid]
  (json-req (str "/release/" mbid)))

(defn release-group
  "Get cover art metadata for a relase-group MBID."
  [mbid]
  (json-req (str "/release-group/" mbid)))
