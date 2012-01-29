(ns actions.action)

(defn new-action
  ([description]
     {:description description
      :priority nil
      :tags []
      :contexts []
      :done false})
  ([description priority tags contexts]
     {:description description
      :priority priority
      :tags tags
      :contexts contexts
      :done false}))
