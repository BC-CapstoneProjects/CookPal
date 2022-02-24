package apilib

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * This class handles creating multiple chrome browers and processing a list of urls
 * Each chrome driver gets its own thread during url processing
 * This class assumes that there are no currently running chrome browsers.
 */
class DriverPool {
    private var chromeProfileIndex = 0
    private val driverPool: Array<ChromeDriver>

    constructor(numberDrivers: Int) {
        driverPool = Array(numberDrivers) { generateChromeDriver() }
    }

    /**
     * Return a list of pages' html given a list of urls
     */
    fun getOutputs(urls: List<String>): List<String> {
        val urlQueue = ConcurrentLinkedQueue(urls)
        val resultQueue = ConcurrentLinkedQueue<String>()

        val threadPool = ArrayList<Thread>()
        for (driver in driverPool) {
            val thread = Thread(ThreadDriver(driver, urlQueue, resultQueue))
            thread.start()
            threadPool.add(thread)
        }

        for (thread in threadPool) {
            thread.join()
        }
        println(resultQueue.size)
        return resultQueue.toList()
    }

    /**
     * Close the chrome browsers
     */
    fun closeDrivers() {
        for (driver in driverPool) {
            driver.close()
        }
    }

    private fun generateChromeDriver(): ChromeDriver {
        WebDriverManager.chromedriver().setup()
        val cp = ChromeOptions()
        cp.addArguments(
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--user-data-dir=C:/ChromeProfiles/Profile$chromeProfileIndex",
            // "--headless"
        )
        chromeProfileIndex++
        return ChromeDriver(cp)
    }

    private class ThreadDriver : Runnable {
        private val driver: ChromeDriver
        private val urlQueue: ConcurrentLinkedQueue<String>
        private val outputQueue: ConcurrentLinkedQueue<String>

        constructor(
            inputDriver: ChromeDriver,
            inputUrls: ConcurrentLinkedQueue<String>,
            outputDocs: ConcurrentLinkedQueue<String>
        ) {
            driver = inputDriver
            urlQueue = inputUrls
            outputQueue = outputDocs
        }

        override fun run() {
            while (!urlQueue.isEmpty()) {
                driver.get(urlQueue.poll())
                outputQueue.add((driver.pageSource))
            }
        }

    }
}