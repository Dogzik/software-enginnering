#pragma once

#include <optional>

template <typename K, typename V>
class cache {
public:
  virtual V const* get(K const& key) const = 0;
  virtual std::optional<V> insert(K const& key, V value) = 0;
  virtual std::optional<V> erase(K const& key) = 0;
  virtual size_t size() const = 0;
  virtual bool contains(K const& key) const {
    return get(key) != nullptr;
  }
  virtual bool empty() const {
    return size() == 0;
  }

  virtual ~cache() = default;
};
