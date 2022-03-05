package old

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import kotlin.concurrent.thread

class DriverMultiTesting {
    var chromeProfileIndex = 0
    fun main() {
        val driver1 = generateChromeDriver()
        val driver2 = generateChromeDriver()
        val driver3 = generateChromeDriver()
        thread { driver1.get("https://medium.com/@korhanbircan/multithreading-and-kotlin-ac28eed57fea") }

        thread { driver2.get("https://www.bing.com/search?q=initialize+array+of+size+kotlin&cvid=baa505011aa74d8a9825159f462fe391&aqs=edge..69i57.6625j0j1&FORM=ANNTA0&PC=U531") }
        thread { driver3.get("https://www.google.com") }


    }

    private fun generateChromeDriver(): ChromeDriver {
        val cp = ChromeOptions()
        cp.addArguments(
            "--no-sandbox", "--disable-dev-shm-usage",
            "--user-data-dir=C:/ChromeProfiles/Profile$chromeProfileIndex"
        )
        chromeProfileIndex++
        return ChromeDriver(cp)
    }

}