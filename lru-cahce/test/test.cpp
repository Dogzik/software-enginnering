#include "lru-cache.h"
#include <gtest/gtest.h>
#include <vector>
#include <random>
#include <ctime>
#include <string>
#include <optional>

auto RANDOM_ENGINE = std::default_random_engine(time(nullptr));

TEST(Trivial, EmptyCache) {
  constexpr size_t CAPACITY = 10;
  lru_cache<int, double> cache(CAPACITY);
  ASSERT_EQ(cache.empty(), true);
  ASSERT_EQ(cache.size(), 0);
  ASSERT_EQ(cache.capacity, CAPACITY);
}

TEST(Trivial, ZeroCapacity) {
  lru_cache<int, int> cache(0);
  ASSERT_EQ(cache.capacity, 0);
  std::uniform_int_distribution<size_t> distribution(1, 100);
  size_t n = distribution(RANDOM_ENGINE);
  std::vector<int> keys(n);
  std::iota(keys.begin(), keys.end(), 0);
  std::shuffle(keys.begin(), keys.end(), RANDOM_ENGINE);
  for (auto key : keys) {
    cache.insert(key, distribution(RANDOM_ENGINE));
  }
  for (auto key : keys) {
    ASSERT_EQ(cache.contains(key), false);
  }
  ASSERT_EQ(cache.size(), 0);
}

TEST(Trivial, Add) {
  lru_cache<int, std::string> cache(5);
  ASSERT_EQ(cache.insert(2, "two"), std::nullopt);
  ASSERT_EQ(cache.insert(3, "three"), std::nullopt);
  ASSERT_EQ(cache.insert(1, "one"), std::nullopt);
  ASSERT_EQ(cache.size(), 3);
  ASSERT_EQ(cache.empty(), false);
  ASSERT_EQ(cache.get(-7), nullptr);
  ASSERT_NE(cache.get(3), nullptr);
  ASSERT_EQ(cache.contains(1),true);
  ASSERT_EQ(*cache.get(2), "two");
}

TEST(Trivial, Delete) {
  lru_cache<int, std::string> cache(5);
  ASSERT_EQ(cache.insert(1, "one"), std::nullopt);
  ASSERT_EQ(cache.insert(2, "two"), std::nullopt);
  ASSERT_EQ(cache.insert(4, "four"), std::nullopt);
  ASSERT_EQ(cache.size(), 3);
  ASSERT_EQ(cache.erase(7), std::nullopt);
  ASSERT_EQ(cache.erase(2), "two");
  ASSERT_EQ(cache.contains(2), false);
  ASSERT_EQ(cache.size(), 2);
}

TEST(Common, AddSameKeys) {
  lru_cache<int, std::string> cache(3);
  cache.insert(1, "1");
  cache.insert(2, "2");
  cache.insert(3, "3");
  cache.insert(1, "one");
  ASSERT_EQ(*cache.get(1), "one");
  cache.insert(4, "4");
  ASSERT_EQ(cache.contains(1), true);
  ASSERT_EQ(cache.get(2), nullptr);
}

TEST(Common, AddDelete) {
  lru_cache<int, std::string> cache(5);
  cache.insert(1, "1");
  cache.insert(2, "2");
  cache.insert(3, "3");
  cache.insert(4, "4");
  ASSERT_EQ(cache.size(), 4);
  ASSERT_EQ(cache.erase(2), "2");
  ASSERT_EQ(cache.contains(2), false);
  ASSERT_EQ(cache.insert(1, "one"), "1");
  ASSERT_EQ(cache.size(), 3);
  ASSERT_EQ(cache.insert(5, "5"), std::nullopt);
  ASSERT_EQ(cache.contains(5), true);
}

TEST(Common, PushingOldOut) {
  lru_cache<int, std::string> cache(3);
  cache.insert(1, "one");
  cache.insert(2, "two");
  cache.insert(3, "three");
  cache.insert(4, "four");
  ASSERT_EQ(cache.contains(1), false);
  cache.insert(2, "2");
  cache.insert(5, "five");
  ASSERT_EQ(*cache.get(2), "2");
  ASSERT_EQ(cache.contains(3), false);
  ASSERT_EQ(cache.insert(4, "4"), "four");
  cache.erase(9);
  ASSERT_EQ(cache.size(), 3);
  ASSERT_EQ(cache.erase(5), "five");
  ASSERT_EQ(cache.size(), 2);
  cache.insert(6, "6");
  ASSERT_EQ(cache.contains(4), true);
}

TEST(Common, AllCombined) {
  constexpr size_t capacity = 5;
  lru_cache<int, std::string> cache(capacity);
  std::uniform_int_distribution<size_t> distribution(capacity + 2, 100);
  size_t n = distribution(RANDOM_ENGINE);
  std::vector<int> keys(n);
  std::iota(keys.begin(), keys.end(), 0);
  std::shuffle(keys.begin(), keys.end(), RANDOM_ENGINE);
  for (auto key: keys) {
    cache.insert(key, std::to_string(key));
  }
  for (size_t i = 0; i < n - capacity; ++i) {
    ASSERT_FALSE(cache.contains(keys[i]));
  }
  for (size_t i = n - capacity; i < n; ++i) {
    ASSERT_TRUE(cache.contains(keys[i]));
    ASSERT_EQ(cache.erase(keys[i]), std::to_string(keys[i]));
  }
  ASSERT_TRUE(cache.empty());
}

int main(int argc, char *argv[]) {
  ::testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
