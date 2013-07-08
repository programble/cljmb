(ns cljmb.core
  (:require [clj-http.client :as http]
            [clojure.string :refer [join]]))

(def ^:dynamic *api* "http://musicbrainz.org/ws/2/")

(defn- req [url params]
  (:body (http/get (str *api* url)
           {:throw-exceptions false
            :as :json
            :coerce :always
            :accept :json
            :query-params (assoc params :fmt "json")})))

(defn- incs [incs]
  {:inc (join \+ (map name incs))})

(defn- offset [per page]
  {:limit per
   :offset (* per (dec page))})

(defn lookup
  "Look up an entity by its MBID."
  ([entity mbid] (lookup entity mbid []))
  ([entity mbid subqueries]
   (req (str (name entity) "/" mbid) (incs subqueries))))

(defn lookup-discid
  "Look up releases associated with a DiscID."
  ([discid] (lookup-discid discid []))
  ([discid subqueries & {:keys [cdstubs toc] :or {cdstubs true}}]
   (req (str "discid/" discid) (merge (incs subqueries)
                                      (when-not cdstubs {:cdstubs "no"})
                                      (when toc {:toc toc})))))

(defn browse
  "Browse for entities linked to an MBID."
  ([entity links per page] (browse entity links [] per page))
  ([entity links subqueries per page]
   (req (name entity) (merge links (incs subqueries) (offset per page)))))

(defn search
  "Search for entities matching a query."
  [entity query per page]
  (req (name entity) (assoc (offset per page) :query query)))
