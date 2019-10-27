#pragma once

#include <boost/intrusive/list.hpp>
#include <cassert>
#include <cstddef>
#include <exception>
#include <unordered_map>

#include "cache.h"

template<typename K, typename V>
class lru_cache final : public cache<K, V> {
  struct value_wrapper : boost::intrusive::list_base_hook<> {
    V value;

    explicit value_wrapper(V value) : value(std::move(value)) {}
  };

  using list_t = boost::intrusive::list<value_wrapper>;

  std::unordered_map<K, value_wrapper> data;
  list_t data_order;

  void erase_last() {
    using node_t = typename std::unordered_map<K, value_wrapper>::value_type;
    auto *node_ptr = reinterpret_cast<node_t *>(reinterpret_cast<char *>(&data_order.back()) -
                                                offsetof(node_t, second));
    data_order.pop_back();
    data.erase(node_ptr->first);
  }

public:
  size_t const capacity;

  explicit lru_cache(size_t capacity) : capacity{capacity} {};

  V const *get(K const &key) const {
    auto it = data.find(key);
    if (it == data.end()) {
      return nullptr;
    } else {
      return &(it->second.value);
    }
  }

  size_t size() const {
    return data.size();
  }

  std::optional<V> erase(K const &key) {
    auto it = data.find(key);
    if (it == data.end()) {
      return std::nullopt;
    }
    auto list_it = data_order.iterator_to(it->second);
    data_order.erase(list_it);
    auto res = std::make_optional(std::move(it->second.value));
    data.erase(it);
    return res;
  }

  std::optional<V> insert(K const &key, V value) {
    if (capacity == 0) {
      return std::nullopt;
    }
    auto prev_size = size();
    auto wrapper = value_wrapper(std::move(value));
    auto[it, was_emplace] = data.try_emplace(key, std::move(wrapper));
    if (!was_emplace) {
      assert(prev_size == size());
      std::swap(wrapper.value, it->second.value);
      auto list_it = data_order.iterator_to(it->second);
      data_order.erase(list_it);
      data_order.push_front(it->second);
      return std::make_optional(std::move(wrapper.value));
    }
    assert(prev_size + 1 == size());
    if (size() > capacity) {
      assert(size() == capacity + 1);
      erase_last();
      assert(size() == capacity);
    }
    data_order.push_front(it->second);
    assert(size() <= capacity);
    return std::nullopt;
  }
};
