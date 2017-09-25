using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TestCache.Caches
{
    interface ICache<TKey, TValue>
    {
        bool TryGetValue(TKey key, out TValue value);
        void Set(TKey key, TValue value);

        void Print();
    }
}
