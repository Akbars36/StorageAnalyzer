using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
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
        private static Storage<String, Int64> storage;

        public const int MAIN_STORAGE_SPEED = 10;

        private const int CACHE_STORAGE_SPEED = 1;

        private const int STRING_LENGTH = 10;


        private static Random rnd = new Random();
        static void Main(string[] args)
        {
            DisplayTimerProperties();
            var primarySize = 100000;
            var primaryMax = 100000;
            var commandsSize = 10000000;
            var cacheSizes = new int[] { 10, 9100, 18190, 27280, 36370, 45460, 54550, 63640, 72730, 81820, 90910, 100000,500000,1000000, 2000000 };
            var sigmas = new int[] { 50000, 100000 };
            Type[] cachePolicyTypes = { typeof(LRUCache<String, Int64>), typeof(MRUCache<String, Int64>) };
            Type[] cacheStoragesTypes = { typeof(Dictionary<String, Node<String, Int64>>), typeof(SortedDictionary<String, Node<String, Int64>>),typeof(SortedList<String, Node<String, Int64>>) };
            var row = 1;
            var cacheSizesLength = cacheSizes.Count();
            var firstColumn = 1;
            var noCacheColumn = firstColumn + 1;
            var firstSizeColumn = noCacheColumn + 1;

            var shift = 1;
            var firstStorageColumn = firstColumn + cacheSizesLength + shift+1;
            var noCacheStorageColumn = noCacheColumn + cacheSizesLength + shift+1;
            var firstSizeStorageColumn = firstSizeColumn + cacheSizesLength + shift+1;

            for (int i = 0; i < 20; i++) {
                var mainStorage = GenerateRandomDictionary(primarySize, primaryMax, STRING_LENGTH);
                for (int cT = 0; cT < cacheStoragesTypes.Count(); cT++)
                {
                    for (int cP = 0; cP < cachePolicyTypes.Count(); cP++)
                    {
                        UpdateExcel("Sheet1", row, firstColumn, cacheStoragesTypes[cT].Name + cachePolicyTypes[cP].Name);
                        row++;
                        UpdateExcel("Sheet1", row, noCacheColumn, "0");
                        UpdateExcel("Sheet1", row, noCacheStorageColumn, "0");
                        for (int cacheSize = 0; cacheSize < cacheSizesLength; cacheSize++)
                        {
                            UpdateExcel("Sheet1", row, firstSizeColumn + cacheSize, cacheSizes[cacheSize] + "");
                            UpdateExcel("Sheet1", row, firstSizeStorageColumn + cacheSize, cacheSizes[cacheSize] + "");
                        }
                        row++;
                        for (int sigma = 0; sigma < sigmas.Count(); sigma++)
                        {
                            var commandsInt = GenerateRandomListGauss(commandsSize, sigmas[sigma]);
                            List<String> commands = GenerateRandomStringList(commandsInt, STRING_LENGTH);
                            UpdateExcel("Sheet1", row, firstColumn, sigmas[sigma] + "");
                            UpdateExcel("Sheet1", row, firstStorageColumn, sigmas[sigma] + "");
                            storage = new Storage<String, Int64>(null, mainStorage);
                            long cacheTime = 0;
                            long storageTime = 0;
                            for (int j = 0; j < commandsSize; j++)
                            {
                                Int64 val;

                                storage.Get(commands[j], out val, ref cacheTime, ref storageTime);
                            }
                            //var totalTime = cacheTime + storageTime * MAIN_STORAGE_SPEED;

                            UpdateExcel("Sheet1", row, noCacheColumn, cacheTime.ToString());
                            UpdateExcel("Sheet1", row, noCacheStorageColumn, storageTime.ToString());
                            //Console.WriteLine("Main storage count = {0} Ticks {1} Cache count {2} Ticks {3} for cache {4} and policy {5}", Storage<Int64, Int64>.CompareCount, storageTime, Storage<Int64, Int64>.CacheCount, cacheTime, cacheStoragesTypes[cT].Name, cachePolicyTypes[cP].Name);
                            for (int cacheSize = 0; cacheSize < cacheSizesLength; cacheSize++)
                            {


                                IDictionary<String, Node<String, Int64>> cacheSorage = (IDictionary<String, Node<String, Int64>>)Activator.CreateInstance(cacheStoragesTypes[cT]);
                                ICache<String, Int64> cache = (ICache<String, Int64>)Activator.CreateInstance(cachePolicyTypes[cP], new object[] { cacheSorage, cacheSizes[cacheSize] });


                                storage = new Storage<String, Int64>(cache, mainStorage);
                                //storage.Print();

                                cacheTime = 0;
                                storageTime = 0;
                                for (int j = 0; j < commandsSize; j++)
                                {
                                    Int64 val;
                                    storage.Get(commands[j], out val,ref cacheTime,ref storageTime);
                                }
                                //totalTime = cacheTime + storageTime * MAIN_STORAGE_SPEED;
                                UpdateExcel("Sheet1", row, cacheSize + firstSizeColumn, cacheTime.ToString());
                                UpdateExcel("Sheet1", row, cacheSize + firstSizeStorageColumn, storageTime.ToString());
                                //commands.ForEach(m => { Int64 val; storage.Get(m, out val); /*Console.Out.WriteLine("\nExtract key = {0}, value = {1}", m, val); storage.Print(); */});
                               // Console.WriteLine("Main storage count = {0} Ticks {1} Cache count {2} Ticks {3} for cache {4} and policy {5}", Storage<Int64, Int64>.CompareCount, storageTime, Storage<Int64, Int64>.CacheCount, cacheTime, cacheStoragesTypes[cT].Name, cachePolicyTypes[cP].Name);
                            }
                            row++;
                        }
                    }
                }
                //row++;
                }

            Console.ReadLine();
        }

        public static void DisplayTimerProperties()
        {
            // Display the timer frequency and resolution.
            if (Stopwatch.IsHighResolution)
            {
                Console.WriteLine("Operations timed using the system's high-resolution performance counter.");
            }
            else
            {
                Console.WriteLine("Operations timed using the DateTime class.");
            }

            long frequency = Stopwatch.Frequency;
            Console.WriteLine("  Timer frequency in ticks per second = {0}",
                frequency);
            long nanosecPerTick = (1000L * 1000L * 1000L) / frequency;
            Console.WriteLine("  Timer is accurate within {0} nanoseconds",
                nanosecPerTick);
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
                oWB = oXL.Workbooks.Open(Environment.CurrentDirectory + @"\Caches3DPlotsDiff.xlsm");
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

        static List<String> GenerateRandomStringList(List<Int64> ints, int stringSize)
        {
            var res = new List<String>(ints.Count);
            for (var i = 0; i < ints.Count; i++)
            {
                var str = "";
                str += ints[i];
                str = str.PadLeft(stringSize, '0');
               // Console.WriteLine(str);
                res.Add(str);
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

        static Dictionary<String, Int64> GenerateRandomDictionary(int count, int maxval, int stringSize)
        {

            var res = new Dictionary<String, Int64>(count);
            for (var i = 0; i < count; i++)
            {
                var key = rnd.Next(0, maxval);
                string str = key+"";
                str = str.PadLeft(stringSize, '0');
                var val = key * (maxval + 1);
                res[str] = val;
            }
            return new Dictionary<String, Int64>(res);
        }

        class SortHelper : IComparer<Int64>, IEqualityComparer<Int64>
        {
            public int Compare(Int64 x, Int64 y)
            {
                Storage<Int64, Int64>.CompareCount += CACHE_STORAGE_SPEED;
                return x.CompareTo(y);
            }

            public bool Equals(long x, long y)
            {
                Storage<Int64, Int64>.CompareCount += CACHE_STORAGE_SPEED;
                return x.Equals(y);
            }

            public int GetHashCode(long obj)
            {
                return obj.GetHashCode();
            }
        }

        class SortMainHelper : IComparer<Int64>, IEqualityComparer<Int64>
        {
            public int Compare(Int64 x, Int64 y)
            {
                Storage<Int64, Int64>.CompareCount += MAIN_STORAGE_SPEED;
                return x.CompareTo(y);
            }

            public bool Equals(long x, long y)
            {
                Storage<Int64, Int64>.CompareCount += MAIN_STORAGE_SPEED;
                return x.Equals(y);
            }

            public int GetHashCode(long obj)
            {
                return obj.GetHashCode();
            }
        }
    }
}
