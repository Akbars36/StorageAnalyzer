using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using TestCache;
using TestCache.Caches;
using Troschuetz.Random;

namespace TestCache
{
    class Program
    {
        private static Storage<Int64, Int64> storage;

        private static Random rnd = new Random();
        static void Main(string[] args)
        {
            //var cacheSize = 10000;
            var primarySize = 1000000;
            var primaryMax = 1000000;
            var commandsSize = 10000000;
            var commandsMax = 1000000;
            var cacheSizes = new int[] { 10, 50, 100, 500, 1000, 5000, 10000 };
            var sigmas = new int[] { 10, 50, 100, 500, 1000, 5000, 10000 };
            Type[] cachePolicyTypes = { typeof(LRUCache<Int64, Int64>), typeof(MRUCache<Int64, Int64>) };
            Type[] cacheStoragesTypes = { typeof(Dictionary<Int64, Node<Int64, Int64>>), typeof(SortedDictionary<Int64, Node<Int64, Int64>>), typeof(SortedList<Int64, Node<Int64, Int64>>) };
            var row = 1;
                    for (int cT = 0; cT < cacheStoragesTypes.Count(); cT++)
                    {
                for (int cP = 0; cP < cachePolicyTypes.Count(); cP++)
                {
                    UpdateExcel("Sheet1", row, 1, cacheStoragesTypes[cT].Name + cachePolicyTypes[cP].Name);
                    row++;
                    for (int cacheSize = 0; cacheSize < cacheSizes.Count(); cacheSize++)
                    {
                        UpdateExcel("Sheet1", row, 1, cacheSizes[cacheSize] + "");
                        for (int sigma = 0; sigma < sigmas.Count(); sigma++)
                        {

                            IDictionary<Int64, Node<Int64, Int64>> cacheSorage = (IDictionary<Int64, Node<Int64, Int64>>)Activator.CreateInstance(cacheStoragesTypes[cT], new object[] { new SortHelper() });
                            ICache<Int64, Int64> cache = (ICache<Int64, Int64>)Activator.CreateInstance(cachePolicyTypes[cP], new object[] { cacheSorage, cacheSizes[cacheSize] });


                            storage = new Storage<Int64, Int64>(cache, GenerateRandomDictionary(primarySize, primaryMax));
                            //storage.Print();
                            var commands = GenerateRandomListGauss(commandsSize, sigmas[sigma]);
                            for (int j = 0; j < commandsSize; j++)
                            {
                                Int64 val;
                                storage.Get(commands[j], out val);
                            }
                            UpdateExcel("Sheet1", row, sigma + 2, Storage<Int64, long>.CompareCount.ToString());
                            //commands.ForEach(m => { Int64 val; storage.Get(m, out val); /*Console.Out.WriteLine("\nExtract key = {0}, value = {1}", m, val); storage.Print(); */});
                            Console.WriteLine("Compares count = {0} for cache {1} and policy {2}", Storage<Int64, Int64>.CompareCount, cacheStoragesTypes[cT].Name, cachePolicyTypes[cP].Name);
                        }
                        row++;
                    }
                }
                }

            Console.ReadLine();
        }

        private static void UpdateExcel(string sheetName, int row, int col, string data)
        {
            System.Threading.Thread.CurrentThread.CurrentCulture = new System.Globalization.CultureInfo("en-US");
            Microsoft.Office.Interop.Excel.Application oXL = null;
            Microsoft.Office.Interop.Excel._Workbook oWB = null;
            Microsoft.Office.Interop.Excel._Worksheet oSheet = null;

            try
            {
                oXL = new Microsoft.Office.Interop.Excel.Application();
                oWB = oXL.Workbooks.Open("C:\\Users\\dpotapov\\Documents\\CacheTest2.xlsm");
                oSheet = String.IsNullOrEmpty(sheetName) ? (Microsoft.Office.Interop.Excel._Worksheet)oWB.ActiveSheet : (Microsoft.Office.Interop.Excel._Worksheet)oWB.Worksheets[sheetName];

                oSheet.Cells[row, col] = data;

                oWB.Save();

                //MessageBox.Show("Done!");
            }
            catch (Exception ex)
            {
                throw ex;
                //MessageBox.Show(ex.ToString());
            }
            finally
            {
                if (oWB != null)
                    oWB.Close();
            }
        }


        static List<Int64> GenerateRandomList(int count, int maxval)
        {
            var res = new List<Int64>(count);
            for (var i = 0; i < count; i++)
            {
                res.Add(rnd.Next(-maxval, maxval));
            }
            return res;
        }

        static List<Int64> GenerateRandomListGauss(int count, int sigma)
        {
            var res = new List<Int64>(count);
            var distr = new NormalDistribution();
            distr.Sigma = sigma;
            for (var i = 0; i < count; i++)
            {
                res.Add(Convert.ToInt64(distr.NextDouble()));
            }
            //res.ForEach(Console.WriteLine);
            return res;
        }

        static Dictionary<Int64, Int64> GenerateRandomDictionary(int count, int maxval)
        {

            var res = new Dictionary<Int64, Int64>(count);
            for (var i = 0; i < count; i++)
            {
                var key = rnd.Next(0, maxval);
                var val = key * (maxval + 1);
                res[key] = val;
            }
            return new Dictionary<Int64, Int64>(res, new SortHelper());
        }

        class SortHelper : IComparer<Int64>, IEqualityComparer<Int64>
        {
            public int Compare(Int64 x, Int64 y)
            {
                Storage<Int64, Int64>.CompareCount += 1;
                return x.CompareTo(y);
            }

            public bool Equals(long x, long y)
            {
                Storage<Int64, Int64>.CompareCount += 1;
                return x.Equals(y);
            }

            public int GetHashCode(long obj)
            {
                //Storage<Int64, Int64>.CompareCount += 1;
                return obj.GetHashCode();
            }
        }
    }
}
