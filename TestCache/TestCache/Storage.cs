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

        public bool Get(TKey key, out TValue value, ref long cacheTime, ref long storageTime)
        {
            value = default(TValue);
            bool existInCache = false;
            if (Cache != null)
            {
                var watch = System.Diagnostics.Stopwatch.StartNew();
                existInCache = Cache.TryGetValue(key, out value);

                watch.Stop();
                var elapsed = watch.ElapsedTicks;
                cacheTime += elapsed;
            }
           
            if (!existInCache)
            {
                if (Cache != null)
                {
                    Cache.Set(key, value);
                }
                CompareCount++;
                var watchSt = System.Diagnostics.Stopwatch.StartNew();
                var existInStorage = PrimaryStorage.TryGetValue(key, out value);

                var elapsedSt = watchSt.ElapsedTicks;
                storageTime += elapsedSt;
                if (!existInStorage)
                {
                    return false;
                }
            }
            else
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
