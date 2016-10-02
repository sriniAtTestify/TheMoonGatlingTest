package testify.gatling.thiemoon

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

/**
  * Created by srinivas on 9/29/2016.
  */
class TheMoonYtelse  extends Simulation {

  val logger = LoggerFactory.getLogger(this.getClass)

  logger.info("START - Building base URL and its configuration...")
  val urlBase = "http://themoon-test-oxid.westeurope.cloudapp.azure.com"
  val urlAPIBaseSuffix = "/api"
  val urlBaseHeader = Map("Upgrade-Insecure-Requests" -> "1", "Content-Type" -> """application/json""", "Accept" -> """application/json""")

  //Http Protocol definition
  val urlBaseHttpConfig = http
    .baseURL(urlBase)
    .inferHtmlResources(BlackList(""".*\.js""", """.*\.css""", """.*\.gif""", """.*\.jpeg""", """.*\.jpg""", """.*\.ico""", """.*\.woff""", """.*\.(t|o)tf""", """.*\.png"""), WhiteList())
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Windows NT 6.2; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")

  logger.info("END - Building base URL and its configuration...")


  //Suffix definition(s)
  logger.info("START - Building Objects - Storefronts, Browse-Storefronts, Store-Categories, Browse-Store-Categories")
  val storefrontSuffix = "/storefronts"
  logger.info(urlAPIBaseSuffix + storefrontSuffix)
  object Storefronts {
    val getStorefronts = exec( http("getStorefrontsList")
      .get(urlAPIBaseSuffix + storefrontSuffix)
      .headers(urlBaseHeader)
      .check(status.is(200))
      .body(StringBody(""" """))
      .asJSON
    )
  } //End of object Storefronts - List of Storefronts

  //Browsing Storefront by Storefront - Clicking on each store gets list of categories and products
  //Browsing Hardcoded storefronts
  object BrowseStorefront {
    val browseStore = exec( http("Browse Store-By-Store")
      .get("/"))
      .pause(2)
      .exec(  http("Store-12")
        .get ("/api/storefronts/12") )
      .pause(2)
      .exec(  http("Store-14")
        .get ("/api/storefronts/14") )
      .pause(2)
      .exec(  http("Store-16")
        .get ("/api/storefronts/16") )
      .pause(2)
  } //End of object BrowseStoreFront


  //Listing Store categories - hardcoded
  val storeCategorySuffix = "/12/categories"
  logger.info(urlAPIBaseSuffix + storefrontSuffix + storeCategorySuffix)
  object StoreCategories {
    val getStoreCategorie = exec( http("getStoreCategoriesList")
      .get(urlAPIBaseSuffix + storefrontSuffix + storeCategorySuffix)
      .headers(urlBaseHeader)
      .check(status.is(200))
      .body(StringBody(""" """))
      .asJSON
    )
  } //End of object Storefronts - List of Storefronts

  //Browse Categories in ONE Storefront - Hardcoded
  object BrowseStoreCategories {
    val browseStoreCategories = exec( http("BrowseStoreCategories")
      .get("/"))
      .pause(2)
      .exec(  http("Store-12 Categories")
        .get ("/api/storefronts/12/categories") )
      .pause(2)
      .exec(  http("Store-14 Categories")
        .get ("/api/storefronts/14/categories") )
      .pause(2)
      .exec(  http("Store-16 Categories")
        .get ("/api/storefronts/16/categories") )
      .pause(2)
  } //End of object BrowseStoreFront
  logger.info("End - Building Objects - Storefronts, Browse-Storefronts, Store-Categories, Browse-Store-Categories")


  //Application Launch Scenario
  val appLaunchScn = scenario("TheMoonYtelse").exec(Storefronts.getStorefronts)

  //'Simple User' - Users that are NOT REGISTERED on TheMoon
  //Simple User Browse Store Scenario Definition
  val simpleUserBrowseStoreScn = scenario("Simple User Browse Store By Store").exec(BrowseStorefront.browseStore)
  //val registeredUsersScn = scenario()

  //Simple User Browse Store Categories Scenario Definition
  val simpleUserBrowseCategoriesInStoreScn = scenario("Simple User Browse Categories in ONE store").exec(BrowseStoreCategories.browseStoreCategories)


  //Test Setup
  //setUp(scn.inject(atOnceUsers(5))).protocols(httpProtocol)
  setUp(
    //appLaunchScn.inject(rampUsers(500) over (5 seconds))
 //   , simpleUserBrowseStoreScn.inject(rampUsers(500) over (5 seconds))
     simpleUserBrowseCategoriesInStoreScn.inject(rampUsers(50) over (20 seconds))
    //, scn.inject(atOnceUsers(6))
  ).protocols(urlBaseHttpConfig)


}

//End of Class
