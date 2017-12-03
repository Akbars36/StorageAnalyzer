using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using TestCache.Caches;

namespace TestCache
{
    class Storage<TKey,TValue>
    {
        ICache<TKey, TValue> Cache { get; set; }

        public Dictionary<TKey, TValue> PrimaryStorage { get; set; }
        public static Int64 CompareCount { get; set; }
        public static Int64 CacheCount { get; set; }

        public Storage(ICache<TKey, TValue> cache)
        {
            this.Cache = cache;
            CompareCount = 0;
            CacheCount = 0;
            this.PrimaryStorage = new Dictionary<TKey, TValue>(); 
        }

        public Storage(ICache<TKey, TValue> cache, Dictionary<TKey, TValue> primaryStorage)
        {
            this.Cache = cache;
            CompareCount = 0;
            CacheCount = 0;
            this.PrimaryStorage = primaryStorage;
        }

        public void Print()
        {
            Console.WriteLine("CompareCount: "+CompareCount);
            Console.WriteLine("PrimaryStorage: ");
            foreach (KeyValuePair<TKey, TValue> kvp in PrimaryStorage)
            {
                Console.WriteLine("Key = {0}, Value = {1}", kvp.Key, kvp.Value);
            }
            Cache.Print();
        }

        public bool Get(TKey key, out TValue value)
        {
            var existInCache = Cache.TryGetValue(key, out value);
            if (!existInCache)
            {
                //CompareCount += Program.MAIN_STORAGE_SPEED;
                if (!PrimaryStorage.TryGetValue(key, out value))
                {
                    return false;
                }
                else
                {
                    Cache.Set(key,value);
                }
            }else
            {
                CacheCount++;
            }
            return true;
        }

        public void Set(TKey key, TValue value)
        {
            PrimaryStorage.Add(key,value);
        }
    }
}
