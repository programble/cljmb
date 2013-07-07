(ns cljmb.core
  (:require [clj-http.client :as http]
            [clojure.string :refer [join]]))

(def ^:dynamic *api* "http://musicbrainz.org/ws/2/")

(defn ^:private req [url params]
  (:body (http/get (str *api* url)
           {:throw-exceptions false
            :as :json
            :coerce :always
            :accept :json
            :query-params (assoc params :fmt "json")})))

(defn ^:private incs [incs]
  {:inc (join \+ (map name incs))})

(defn ^:private offset [per page]
  {:limit per
   :offset (* per (dec page))})

(defn lookup
  "Look up an entity by its MBID."
  ([entity mbid] (lookup entity mbid []))
  ([entity mbid subqueries]
   (req (str (name entity) "/" mbid) (incs subqueries))))

;; TODO: Non-MBID lookups

(defn browse
  "Browse for entities linked to an MBID."
  ([entity links per page] (browse entity links [] per page))
  ([entity links subqueries per page]
   (req (name entity) (merge links (incs subqueries) (offset per page)))))

(defn search
  "Search for entities matching a query."
  [entity query per page]
  (req (name entity) (assoc (offset per page) :query query)))
