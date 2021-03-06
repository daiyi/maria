(ns maria.views.repl-values
  (:require [clojure.string :as string]
            [maria.messages :as messages]
            [re-view-material.icons :as icons]
            [re-view.util :as v-util]
            [re-view.core :as v :refer [defview]]
            [maria.user.shapes :as shapes]
            [cljs.pprint :refer [pprint]]
            [maria.magic-tree :as magic]
            [maria.editor :as editor]
            [maria.source-lookups :as source-lookups]
            [maria.views.repl-specials :as special-views])
  (:import [goog.async Deferred]))

(defn bracket-type [value]
  (cond (vector? value) ["[" "]"]
        (set? value) ["#{" "}"]
        :else ["(" ")"]))

(declare format-value)

(defview display-deferred
  {:life/will-mount (fn [{:keys [deferred view/state]}]
                      (-> deferred
                          (.addCallback #(swap! state assoc :value %1))
                          (.addErrback #(swap! state assoc :error %)))

                      )}
  [{:keys [view/state]}]
  (let [{:keys [value error] :as s} @state]
    [:div
     [:.gray.i "goog.async.Deferred"]
     [:.pv3 (cond (nil? s) [:.progress-indeterminate]
                  error (str error)
                  :else (or (some-> value (format-value)) [:.gray "Finished."]))]]))

(def expander-outter :.dib.bg-darken.ph2.pv1.ma1.br2.pointer)
(def expander-label :.inline-flex.items-center)

(defview format-collection
  {:life/initial-state 20}
  [{limit :view/state} value]
  (let [[lb rb] (bracket-type value)
        more? (= (count (take (inc @limit) value)) (inc @limit))]
    [:div
     [:span.output-bracket lb]
     (interpose " " (v-util/map-with-keys format-value (take @limit value)))
     (when more? [expander-outter {:on-click #(swap! limit + 20)} [expander-label "…"]])
     [:span.output-bracket rb]]))

(defview format-function
  {:life/initial-state (fn [_ value] {:expanded? false})}
  [{:keys [view/state]} value]
  (let [{:keys [expanded?]} @state
        fn-name (some-> (source-lookups/fn-name value) (symbol) (name))]

    [:span.i
     [expander-outter {:on-click #(swap! state update :expanded? not)}
      [expander-label
       (if (and fn-name (not= "" fn-name))
         (some-> (source-lookups/fn-name value) (symbol) (name))
         [:span.o-50.mr1 "ƒ"])
       (-> (if expanded? icons/ArrowDropUp
                         icons/ArrowDropDown)
           (icons/size 20)
           (icons/class "mln1 mrn1 o-50"))]
      (when expanded?
        (or (some-> (source-lookups/js-source->clj-source (.toString value))
                    (editor/viewer))
            (some-> (source-lookups/fn-var value)
                    (special-views/var-source))))]]))

(defn format-value [value]
  [:span
   (cond
     (= :shape (:is-a value)) (shapes/show value)           ; synthesize component for shape
     (or (vector? value)
         (seq? value)
         (set? value)) (format-collection value)
     (var? value) (.toString value)
     (fn? value) (format-function value)
     (v/is-react-element? value) value
     (instance? cljs.core/Namespace value) (str value)
     (instance? Deferred value) (display-deferred {:deferred value})
     :else (if (nil? value)
             "nil"
             (try (string/trim-newline (with-out-str (pprint value)))
                  (catch js/Error e "error printing result"))))])

(defview display-result
  {:key :id}
  [{:keys [value
           error
           error-location
           warnings
           source] :as result}]
  (let [error? (or error (seq warnings))]
    (when error
      (.error js/console error))
    (when (seq warnings)
      (prn :warnings warnings))
    [:div.bb.b--darken.overflow-hidden
     {:class (when error? "bg-darken-red")}
     (when source
       [:.code.overflow-auto.pre.gray.mv3
        {:style {:max-height 200}}
        (editor/viewer {:error-ranges (cond-> []
                                              error (conj (magic/error-range source error-location))
                                              (seq warnings) (into (map #(magic/error-range source (:env %)) warnings)))} source)])
     [:.ws-prewrap.relative.mv3
      {:style {:max-height 500
               :overflow-y "auto"}}
      (cond error?
            [:.ph3.overflow-auto
             (for [message (cons (when error (messages/reformat-error result))
                                 (map messages/reformat-warning (distinct warnings)))
                   :when message]
               [:.mv2 message])]
            (v/is-react-element? value)
            value
            :else [:.ph3 (format-value value)])]]))

(defn repl-card [& content]
  (into [:.sans-serif.bg-white.shadow-4.ma2] content))
